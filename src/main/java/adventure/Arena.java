package adventure;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.yaml.snakeyaml.Yaml;

import adventure.actions.Trigger;
import adventure.actions.Trigger.Event;
import adventure.actions.Trigger.Verb;

public class Arena {
	
    private Map<String, Location> locations;
    private Associations associations;
	private String startLabel;
	
	private static Arena instance;
	
	public static Arena getInstance() {
	    return instance;
	}
	
	public Arena (String startLabel) {
		this.locations = new HashMap<>();
		this.associations = new Associations();
		this.startLabel = startLabel;
		instance = this;
	}
	
	public void addLocation(String label, String name, String abbrev, String description) {
		if (locations.get(label) != null) {
			throw new ArenaSyntaxException("location '" + label + "' already exists");
		}
		Location location = new Location(
		        label, name,  
		        ((abbrev != null) ? abbrev : label),
		        description);
		locations.put(label, location);
	}

	public void addExit(String fromLabel, String toLabel, Item portal, Direction direction) {
		Location fromLoc = locations.get(fromLabel);
		if (fromLoc == null) {
			throw new ArenaSyntaxException("location '" + fromLabel + "' not found");
		}
		Location toLoc = locations.get(toLabel);
        if (toLoc == null) {
			throw new ArenaSyntaxException("location '" + toLabel + "' not found");
		}
        
        Exit exit = new Exit(direction, portal, toLoc);
        fromLoc.addExit(exit);
	}
	
	public void addExit(String from, String to, Item portal,
	        Direction forward, Direction back) {
		addExit(from, to, portal, forward);
		addExit(to, from, portal, back);
	}
	
	public Item addItem(String kind, String location, String label, String name, String abbrev, 
	        String description, long flags, List<Trigger> triggers) {
	    if (abbrev == null) {
	        abbrev = label;
	    }
	    Item item = ("CONTAINER".equals(kind)) ?
                new Container(label, name, abbrev, description, flags, triggers) :
	            new Item(label, name, abbrev, description, flags, triggers);
	    if (location != null) {
	        // A portal 'item' doesn't have a (single) location ...
	        Location loc = locations.get(location);
	        if (loc == null) {
	            throw new ArenaSyntaxException("location '" + location + "' not found");
	        }
	        loc.addItem(item);
	    }
        return item;
	}
	
	public Location getStart() {
		Location start = locations.get(startLabel);
		if (start == null) {
			throw new ArenaSyntaxException("start location '" + startLabel + "' not found");
		}
		return start;
	}
	
	public Associations getAssociations() {
	    return associations;
	}
	
	@SuppressWarnings("unchecked")
	public static Arena load(InputStream is) {
		Yaml yaml = new Yaml();
		Map<String, Object> loaded = yaml.load(is);
		
		Arena arena = new Arena((String) loaded.get("start"));
		
		List<Object> locations = (List<Object>) loaded.get("locations");
		for (Object data: locations) {
			Map<String, String> locData = (Map<String, String>) data;
			arena.addLocation(locData.get("label"), locData.get("name"), 
			        locData.get("abbrev"), locData.get("description"));
		}
		
        List<Object> exits = (List<Object>) loaded.get("exits");
		for (Object data: exits) {
			Map<String, Object> exitData = (Map<String, Object>) data;
            Item portal = arena.parsePortal(exitData);
			if (exitData.containsKey("backwards")) {
				arena.addExit(getString(exitData, "from"), getString(exitData, "to"), 
						portal,
						Direction.valueOf(getString(exitData, "forwards")),
						Direction.valueOf(getString(exitData, "backwards")));
			} else {
				arena.addExit(getString(exitData, "from"), getString(exitData, "to"), 
					    portal, 
					    Direction.valueOf(getString(exitData, "forwards")));
			}
		}
		
	    arena.parseItems(loaded);
	    
	    List<Object> associationData = (List<Object>) loaded.get("associations");
	    for (Object relationData: associationData) {
	        Map<String, String> data = (Map<String, String>) relationData;
	        arena.associations.addRelation(
	                data.get("relation"), data.get("item1"), data.get("item2"));
	    }
	    
		return arena;
	}

    private Item parsePortal(Map<String, Object> exitData) {
        Object portalData = exitData.get("portal");
        if (portalData == null) {
            return null;
        } else {
            return parseItem(portalData);
        }
    }

    @SuppressWarnings("unchecked")
    private void parseItems(Map<String, Object> loaded) {
        List<Object> items = (List<Object>) loaded.get("items");
	    for (Object data: items) {
	        parseItem(data);
	    }
    }

    @SuppressWarnings("unchecked")
    private Item parseItem(Object data) {
        Map<String, Object> itemData = (Map<String, Object>) data;
        long flags = parseItemFlags(getString(itemData, "flags"));
        List<Trigger> triggers = parseTriggers(itemData);
        Item item = addItem(
                getString(itemData, "kind"), 
                getString(itemData, "location"), 
                getString(itemData, "label"), 
                getString(itemData, "name"), 
                getString(itemData, "abbrev"), 
                getString(itemData, "description"), 
                flags, triggers);
        if ("CONTAINER".equals(getString(itemData, "kind"))
                && itemData.get("contents") != null) {
            List<Object> contents = (List<Object>) itemData.get("contents");
            for (Object content: contents) {
                Item contentItem = parseItem(content);
                ((Container) item).addItem(contentItem);
            }
        }
        return item;
    }

    private static String getString(Map<String, Object> data, String tag) {
        return (String) data.get(tag);
    }

    @SuppressWarnings("unchecked")
    private List<Trigger> parseTriggers(Map<String, Object> itemData) {
        List<Trigger> triggers = new ArrayList<>();
        List<Object> data = (List<Object>) itemData.get("triggers");
        if (data != null) {
            for (Object obj: data) {
                Map<String, String> triggerData = (Map<String, String>) obj;
                List<Event> events = parseEvents(triggerData.get("events"));
                if (events.isEmpty()) {
                    throw new ArenaSyntaxException("A trigger must have 1 or more events");
                }
                Trigger trigger = new Trigger(
                        events,
                        triggerData.get("target"),
                        parseVerb(triggerData.get("verb")),
                        triggerData.get("message"));
                triggers.add(trigger);
            }
        }
        return triggers;
    }

    private Verb parseVerb(String string) {
        return string == null ? null : Verb.valueOf(string.toUpperCase());
    }

    private List<Event> parseEvents(String string) {
        String[] words = (string == null ? "" : string).split("\\s+");
        return Stream.of(words)
                .map(String::toUpperCase)
                .map(Event::valueOf).collect(Collectors.toList());
    }

    private long parseItemFlags(String flagString) {
        long flags = 0L;
        if (flagString != null) {
            String[] words = flagString.split("\\s+");
            for (String word: words) {
                Long flag = Item.ITEM_FLAGS.get(word.toUpperCase());
                if (flag == null) {
                    throw new ArenaSyntaxException("Unknown flag '" + word + "'");
                }
                flags |= flag.longValue();
            }
        }
        return flags;
    }
}
