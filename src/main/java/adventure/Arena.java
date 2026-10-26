package adventure;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.yaml.snakeyaml.Yaml;

import adventure.Location.LocationType;
import adventure.actions.Trigger;
import adventure.actions.Trigger.Event;
import adventure.actions.Trigger.Verb;

public class Arena {
	
    private Map<String, Location> allLocations;
    private Map<String, Actor> allActors;
    private Associations associations;
	private String startLabel;
	
	private static Arena instance;
	
	public static Arena getInstance() {
	    return instance;
	}
	
	public Arena (String startLabel) {
		this.allLocations = new HashMap<>();
		this.allActors = new HashMap<>();
		this.associations = new Associations();
		this.startLabel = startLabel;
		instance = this;
	}
	
	public void addExit(String fromLabel, String toLabel, Item portal, String directions,
	        String description) {
		Location fromLoc = parseLocation(fromLabel);
		Location toLoc = parseLocation(toLabel);
        Direction[] dirs = parseDirections(directions);
        Vista exit = new Exit(dirs, portal, toLoc, description);
        fromLoc.addVista(exit);
	}

    private static Direction[] parseDirections(String directions) {
        Direction[] dirs = Arrays.stream(directions.split("\\s+"))
                .map((s) -> Direction.valueOf(s))
                .toArray(Direction[]::new);
        return dirs;
    }
    
    public void addExit(String from, String to, Item portal,
	        String forward, String back, String description) {
		addExit(from, to, portal, forward, description);
		addExit(to, from, portal, back, description);
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
	        String[] parts = location.split("#", 2);
	        Location loc = parseLocation(parts[0]);
	        if (parts.length == 1) {
	            loc.addItem(item);
	        } else {
	            Direction dir = Direction.valueOf(parts[1]);
	            Optional<Vista> opt = loc.getVistas().stream()
	                    .filter((v) -> v.matchesDirection(dir))
	                    .findFirst();
	            Vista vista;
	            if (opt.isPresent() && (vista = opt.get()) instanceof Wall) {
	                ((Wall) vista).getItemsOnWall().add(item);
	            } else {
	                throw new ArenaSyntaxException("no wall '" + location + "' found");
	            }
	        }
	    }
        return item;
	}
	
	public Actor addActor(String label, String kind, String location, 
	        String name, String abbrev, String description) {
	    Location loc = parseLocation(location);
	    Actor actor = new Actor(Actor.Kind.valueOf(kind), label, name,
	            abbrev, description, loc);
	    allActors.put(label, actor);
	    return actor;
	}
	
	private Location parseLocation(String location) {
	    Location loc = allLocations.get(location);
        if (loc == null) {
            throw new ArenaSyntaxException("location '" + location + "' not found");
        }
        return loc;
    }

    public Location getStart() {
		Location start = allLocations.get(startLabel);
		if (start == null) {
			throw new ArenaSyntaxException("start location '" + startLabel + "' not found");
		}
		return start;
	}
    
    public List<Actor> getAllActors() {
        return new ArrayList<>(allActors.values());
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
			Map<String, Object> locData = (Map<String, Object>) data;
			String label = getString(locData, "label");
			String name = getString(locData, "name");
            String abbrev = getString(locData, "abbrev");
            String description = getString(locData, "description");
	        if (arena.allLocations.get(label) != null) {
	            throw new ArenaSyntaxException(
	                    "location '" + label + "' already exists");
	        }
	        LocationType kind = LocationType.valueOf(getString(locData, "type"));
	        Location location = new Location(kind, label, name, abbrev, description);
	        
	        List<Map<String, Object>> wallData = 
	                (List<Map<String, Object>>) locData.get("walls");
	        if (wallData != null) {
	            for (Map<String, Object> wall: wallData) {
	                location.addVista(
	                        new Wall(Direction.valueOf(
	                                getString(wall, "direction")),
	                                getString(wall, "name"), 
	                                getString(wall, "description")));
	            }
	        }

	        List<Map<String, Object>> vistaData = 
	                (List<Map<String, Object>>) locData.get("vistas");
	        if (vistaData != null) {
	            for (Map<String, Object> vista: vistaData) {
	                Direction[] directions = 
	                        parseDirections(getString(vista, "direction"));
	                for (Direction direction: directions) {
	                    location.addVista(
	                            new Vista(direction, 
	                                    getString(vista, "description")));
	                }
	            }
	        }
	        arena.allLocations.put(label, location);
		}
		
        List<Object> exits = (List<Object>) loaded.get("exits");
		for (Object data: exits) {
			Map<String, Object> exitData = (Map<String, Object>) data;
            Item portal = arena.parsePortal(exitData);
			if (exitData.containsKey("backwards")) {
				arena.addExit(getString(exitData, "from"), 
				        getString(exitData, "to"), 
						portal,
						getString(exitData, "forwards"),
						getString(exitData, "backwards"),
						getString(exitData, "description"));
			} else {
				arena.addExit(getString(exitData, "from"), 
				        getString(exitData, "to"), 
					    portal, 
					    getString(exitData, "forwards"),
                        getString(exitData, "description"));
			}
		}
				
	    arena.parseItems(loaded);
	    
	    List<Object> creatures = (List<Object>) loaded.get("creatures");
	    for (Object data: creatures) {
	        Map<String, Object> creatureData = (Map<String, Object>) data;
	        arena.addActor(
	                getString(creatureData, "label"), 
	                getString(creatureData, "kind"), 
	                getString(creatureData, "location"), 
	                getString(creatureData, "name"), 
	                getString(creatureData, "abbrev"), 
	                getString(creatureData, "description"));
	    }
	    
	    List<Object> associationData = (List<Object>) loaded.get("associations");
	    for (Object relationData: associationData) {
	        Map<String, Object> data = (Map<String, Object>) relationData;
	        arena.associations.addRelation(
	                getString(data, "relation"), 
	                getString(data, "item1"), 
	                getString(data, "item2"));
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
        String res = (String) data.get(tag);
        return (res != null) ? res.trim() : null;
    }
    
    @SuppressWarnings("unchecked")
    private List<Trigger> parseTriggers(Map<String, Object> itemData) {
        List<Trigger> triggers = new ArrayList<>();
        List<Object> data = (List<Object>) itemData.get("triggers");
        if (data != null) {
            for (Object obj: data) {
                Map<String, Object> triggerData = (Map<String, Object>) obj;
                List<Event> events = parseEvents(getString(triggerData, "events"));
                if (events.isEmpty()) {
                    throw new ArenaSyntaxException("A trigger must some events");
                }
                Trigger trigger = new Trigger(
                        events,
                        getString(triggerData, "target"),
                        parseVerb(getString(triggerData, "verb")),
                        getString(triggerData, "message"));
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
