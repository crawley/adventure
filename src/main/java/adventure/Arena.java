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

import adventure.Item.FlagDescriptor;
import adventure.Location.LocationType;
import adventure.Template.Kind;
import adventure.actions.Trigger;
import adventure.actions.Trigger.Event;
import adventure.actions.Triggered;
import adventure.actions.Triggered.Verb;

public class Arena {
	
    private Map<String, Location> allLocations;
    private Map<String, Actor> allActors;
    private Associations associations;
	private String startLabel;
	private Actor player;
	
	private static Arena theArena;
	
	public static Arena getInstance() {
	    return theArena;
	}
	
	public Arena (String startLabel) {
		this.allLocations = new HashMap<>();
		this.allActors = new HashMap<>();
		this.associations = new Associations();
		this.startLabel = startLabel;
		theArena = this;
	}
	
	public void makeExit(String fromLabel, String toLabel, Item portal, String directions,
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
    
    public void makeExits(String from, String to, Item portal,
	        String forward, String back, String description) {
		makeExit(from, to, portal, forward, description);
		makeExit(to, from, portal, back, description);
	}
	
	public Item makeItem(String kind, String location, String label, String name, 
	        String abbrev, String description, long flags, 
	        Template base, List<Trigger> triggers, PropertySet props) {
	    if (abbrev == null) {
	        abbrev = label;
	    }
	    Item item = ("CONTAINER".equals(kind)) ?
                new Container(label, name, abbrev, description, flags, base, triggers, props) :
	            new Item(label, name, abbrev, description, flags, base, triggers, props);
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
	                throw new ArenaException("no wall '" + location + "' found");
	            }
	        }
	    }
        return item;
	}
	
	public Entity makeActor(String label, String kind, String location, 
	        String name, String abbrev, String description, Template base,
	        List<Trigger> triggers, PropertySet props) {
	    Location loc = parseLocation(location);
	    Actor.Kind actorKind = Actor.Kind.valueOf(kind);
	    Actor actor = switch (actorKind) {
	    case PLAYER, NPC -> new Person(
	            actorKind, label, name, abbrev, description, loc, 
	            base, triggers, props);
	    case PET -> new Pet(
	            actorKind, label, name, abbrev, description, loc, 
	            base, triggers, props);
	    default -> null;
	    };
	    allActors.put(label, actor);
	    return actor;
	}
	
	public void addActor(Actor actor) {
	    String label = actor.getLabel();
	    if (allActors.get(label) != null) {
	        throw new ArenaException(
	                "Actor with label '" + label + "' already exists");
	    }
	    allActors.put(label, actor);
	}
	
	private Location parseLocation(String location) {
	    Location loc = allLocations.get(location);
        if (loc == null) {
            throw new ArenaException("location '" + location + "' not found");
        }
        return loc;
    }

    public Location getStart() {
		Location start = allLocations.get(startLabel);
		if (start == null) {
			throw new ArenaException("start location '" + startLabel + "' not found");
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
		
		parseTemplates(loaded, "location_templates", Template.Kind.LOCATION);
		parseTemplates(loaded, "actor_templates", Template.Kind.ACTOR);
		parseTemplates(loaded, "item_templates", Template.Kind.ITEM);

		List<Object> locations = (List<Object>) loaded.get("locations");
		for (Object data: locations) {
			Map<String, Object> locData = (Map<String, Object>) data;
			String label = getString(locData, "label");
			String name = getString(locData, "name");
            String abbrev = getOptString(locData, "abbrev");
            String description = getString(locData, "description");
	        if (arena.allLocations.get(label) != null) {
	            throw new ArenaException(
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
				arena.makeExits(getString(exitData, "from"), 
				        getString(exitData, "to"), 
						portal,
						getString(exitData, "forwards"),
						getString(exitData, "backwards"),
						getOptString(exitData, "description"));
			} else {
				arena.makeExit(getString(exitData, "from"), 
				        getString(exitData, "to"), 
					    portal, 
					    getString(exitData, "forwards"),
                        getOptString(exitData, "description"));
			}
		}
				
	    arena.parseItems(loaded);
	    
	    List<Object> creatures = (List<Object>) loaded.get("creatures");
	    for (Object data: creatures) {
	        Map<String, Object> creatureData = (Map<String, Object>) data;
	        List<Trigger> triggers = parseTriggers(creatureData);
	        PropertySet props = parseProperties(creatureData);
	        Template base = parseBaseReference(creatureData);
	        arena.makeActor(
	                getString(creatureData, "label"), 
	                getString(creatureData, "kind"), 
	                getString(creatureData, "location"), 
	                getString(creatureData, "name"), 
	                getString(creatureData, "abbrev"), 
	                getString(creatureData, "description"),
                    base,
	                triggers,
	                props);
	    }
	    
	    List<Object> associationData = (List<Object>) loaded.get("associations");
	    for (Object relationData: associationData) {
	        Map<String, Object> data = (Map<String, Object>) relationData;
	        arena.associations.addRelationship(
	                getString(data, "relation"), 
	                getString(data, "entity1"), 
	                getString(data, "entity2"));
	    }
	    
		return arena;
	}

    @SuppressWarnings("unchecked")
    private static void parseTemplates(Map<String, Object> data, 
            String tag, Kind kind) {
        List<Object> templates = (List<Object>) data.get(tag);
        if (templates == null) {
            return;
        }
        for (Object obj: templates) {
            Map<String, Object> templateData = (Map<String, Object>) obj;
            List<Trigger> triggers = parseTriggers(templateData);
            PropertySet properties = parseProperties(templateData);
            Template base = parseBaseReference(templateData);
            String label = getString(templateData, "label");
            new Template(kind, label, base, triggers, properties);
        }
    }

    private static Template parseBaseReference(Map<String, Object> templateData) {
        String name = getOptString(templateData, "base");
        Template base = (name == null) ? null : Template.lookup(name);
        return base;
    }

    @SuppressWarnings("unchecked")
    private static PropertySet parseProperties(Map<String, Object> data) {
        PropertySet props = new PropertySet();
        List<Object> propsData = (List<Object>) data.get("properties");
        if (propsData != null) {
            for (Object obj: propsData) {
                Map<String, Object> propData = (Map<String, Object>) obj;
                String name = getString(propData, "name");
                String type = getString(propData, "type");
                Property prop;
                if (type.equals("boolean")) {
                    prop = new BooleanProperty(name, 
                            getBoolean(propData, "initial"));
                } else if (type.equals("int")) {
                    prop = new IntegerProperty(name, 
                            getInteger(propData, "initial"), 
                            getOptInteger(propData, "min", Integer.MIN_VALUE), 
                            getOptInteger(propData, "max", Integer.MAX_VALUE));
                } else if (type.equals("float")) {
                    prop = new FloatProperty(name, 
                            getFloat(propData, "initial"), 
                            getOptFloat(propData, "min", Float.MIN_VALUE), 
                            getOptFloat(propData, "max", Float.MAX_VALUE));
                } else {
                    throw new ArenaException(
                            "property type '" + type + "' not recognized");
                }
                props.add(prop);
            }
        }
        return props;
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
        long flags = parseItemFlags(getOptString(itemData, "flags"));
        List<Trigger> triggers = parseTriggers(itemData);
        PropertySet props = parseProperties(itemData);
        Template base = parseBaseReference(itemData);
        Item item = makeItem(
                getOptString(itemData, "kind"), 
                getOptString(itemData, "location"), 
                getString(itemData, "label"), 
                getString(itemData, "name"), 
                getOptString(itemData, "abbrev"), 
                getOptString(itemData, "description"), 
                flags, base, triggers, props);
        if ("CONTAINER".equals(getOptString(itemData, "kind"))
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
        return getString(data, tag, false);
    }
    
    private static String getOptString(Map<String, Object> data, String tag) {
        return getString(data, tag, true);
    }
    
    private static String getString(
            Map<String, Object> data, String tag, boolean optional) {
        Object obj = data.get(tag);
        if (obj == null) {
            if (optional) {
                return null;
            } else {
                throw new ArenaException("Missing '" + tag + "' attribute");
            }
        } else if (obj instanceof String) {
            return ((String) obj).trim();
        } else if (obj instanceof Number) {
            return obj.toString();
        } else {
            throw new ArenaException("Expected '" + tag 
                    + "' to be a simple attribute (not a " 
                    + obj.getClass() + ")");
        }
    }

    private static boolean getBoolean(Map<String, Object> data, String tag) {
        return parseBoolean(tag, getString(data, tag));
    }
    
    private static boolean getOptBoolean(Map<String, Object> data, 
            String tag, boolean defaultValue) {
        String value = getOptString(data, tag);
        if (value == null) {
            return defaultValue;
        } else {
            return parseBoolean(tag, value);
        }
    }

    private static boolean parseBoolean(String tag, String value) {
        switch (value.trim().toUpperCase()) {
        case "Y", "YES", "TRUE", "1": return true;
        case "N", "NO", "FALSE", "0": return false;
        default:
            throw new ArenaException(
                    "Expected a boolean for '" + tag + "'");
        }
    }
    
    private static int getInteger(Map<String, Object> data, String tag) {
        try {
            return Integer.parseInt(getString(data, tag));
        } catch (NumberFormatException ex) {
            throw new ArenaException(
                    "Value of '" + tag + "' is not an integer",
                    ex);
        }
    }
    
    private static int getOptInteger(Map<String, Object> data, 
            String tag, int defaultValue) {
        String value = getOptString(data, tag);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new ArenaException(
                    "Value of '" + tag + "' is not an integer", ex);
        }
    }
    
    private static float getFloat(Map<String, Object> data, String tag) {
        try {
            return Float.parseFloat(getString(data, tag));
        } catch (NumberFormatException ex) {
            throw new ArenaException(
                    "Value of '" + tag + "' is not an float", ex);
        }
    }
    
    private static float getOptFloat(Map<String, Object> data, 
            String tag, float defaultValue) {
        String value = getOptString(data, tag);
        if (value == null) {
            return defaultValue;
        } 
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException ex) {
            throw new ArenaException(
                    "Value of '" + tag + "' is not an float", ex);
        }
    }
    
    @SuppressWarnings("unchecked")
    private static List<Trigger> parseTriggers(Map<String, Object> itemData) {
        List<Trigger> triggers = new ArrayList<>();
        List<Object> data = (List<Object>) itemData.get("triggers");
        if (data != null) {
            for (Object obj: data) {
                Map<String, Object> triggerData = (Map<String, Object>) obj;
                List<Event> events = parseEvents(getString(triggerData, "events"));
                if (events.isEmpty()) {
                    throw new ArenaException("A trigger must some events");
                }
                List<Triggered> actions = parseActions(
                        (List<Object>) triggerData.get("actions"));
                boolean override = getOptBoolean(triggerData, "override", false);
                Trigger trigger = new Trigger(events, actions, override);
                triggers.add(trigger);
            }
        }
        return triggers;
    }

    @SuppressWarnings("unchecked")
    private static List<Triggered> parseActions(List<Object> actionsData) {
        List<Triggered> actions = new ArrayList<>();
        for (Object data: actionsData) {
            Map<String, Object> actionData = (Map<String, Object>) data;
            Triggered action = new Triggered(
                    getOptString(actionData, "target"),
                    getOptString(actionData, "arg"),
                    getOptString(actionData, "arg2"),  
                    parseVerb(getString(actionData, "verb")), 
                    getOptString(actionData, "message"), 
                    !getOptBoolean(actionData, "onetime", false));
            actions.add(action);
        }
        return actions;
    }

    private static Verb parseVerb(String string) {
        return Verb.valueOf(string.toUpperCase());
    }

    private static List<Event> parseEvents(String string) {
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
                FlagDescriptor flag = Item.ITEM_FLAGS.get(word.toUpperCase());
                if (flag == null) {
                    throw new ArenaException("Unknown flag '" + word + "'");
                }
                flags |= flag.flag();
            }
        }
        return flags;
    }

    public void setPlayer(Actor player) {
        this.player = player;
    }
    
    public Actor getPlayer() {
        return this.player;
    }
}
