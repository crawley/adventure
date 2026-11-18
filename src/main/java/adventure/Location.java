package adventure;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adventure.Triggered.Verb;
import adventure.actions.ActionResult;
import adventure.actions.ActionResult.Outcome;

public class Location extends Entity {
    
    public enum LocationType {
        ROOM("in"), PASSAGEWAY("in"), PLATFORM("on"), STAIRS("on"),
        PATH("on"), FRONT("in front of"), BEHIND("behind"),
        AREA1("in"), AREA2("on");
        
        private final String preposition;
        
        LocationType(String preposition) {
            this.preposition = preposition;
        }

        public String getPreposition() {
            return preposition;
        }
    }
    
    public static final long DARK = 0x1L;
    public static final long HAS_LIGHT = 0x2L;
        
    @SuppressWarnings("serial")
    public static final Map<String, FlagDescriptor> LOCATION_FLAGS = new HashMap<>(){
        {
            put("DARK", new FlagDescriptor (DARK));
            put("HAS_LIGHT", new FlagDescriptor (HAS_LIGHT, "light_on", false));
        }
    };
	
	private final List<Vista> vistas;
	private final EntityList<Item> items;
	private final LocationType type;

    public Location(LocationType type, String label, String name, 
            String abbrev, String description, long flags, 
            Template base, List<Trigger> triggers, PropertySet properties) {
		super(label, name, abbrev, description, flags, base, triggers, properties);
		this.type = type;
		this.vistas = new ArrayList<>();
		this.items = new EntityList<>("here");
	}

	public LocationType getType() {
        return type;
    }

    public List<Vista> getVistas() {
        return vistas;
    }
    
    public void addVista(Vista newVista) {
        EnumSet<Direction> newDirections = 
                EnumSet.copyOf(Arrays.asList(newVista.getDirections()));
        for (Vista vista: vistas) {
            if (vista.getClass() != newVista.getClass()) {
                continue;
            }
            for (Direction direction: vista.getDirections()) {
                if (newDirections.contains(direction)) {
                    throw new ArenaException(
                            "Duplicate " + newVista.getClass().getSimpleName()
                            + " for direction " + direction 
                            + " in vistas for location '" + getLabel() + "'");
                }
            }
        }
        vistas.add(newVista);
    }

    public List<Exit> getExits() {
		return vistas.stream()
		        .filter((v) -> v instanceof Exit)
		        .map((v) -> (Exit) v)
		        .toList();
	}

	public List<Item> getItems() {
	    return items;
	}
	    
	public void addItem(Item item) {
	    items.add(item);
	}

	public void removeItem(Entity item) {
	    items.remove(item);
	}
	
    public boolean isDark() {
        return (getFlags() & DARK) != 0;
    }
    
    public boolean isBlocked() {
        Property prop = this.properties.lookup("blocked");
        return prop != null && prop.isTrue();
    }
    
    public void setBlocked(boolean blocked) {
        Property prop = this.properties.lookup("blocked");
        if (prop != null) {
            ((BooleanProperty) prop).setValue(blocked);
        }
    }    

    public static Location lookupLocation(String label) {
        Entity entity = Entity.lookup(label);
        if (entity == null) {
            throw new EntityException("No entity with label '" + label + "'");
        } else if (entity instanceof Item) {
            return (Location) entity;
        } else {
            throw new EntityException(
                    "Entity with label '" + label + "' is not a location");
        }
    }

    public List<Item> portalItems() {
        List<Item> list = vistas.stream()
                .filter((v) -> v instanceof Exit)
                .map((v) -> (Exit) v).map((exit) -> exit.getPortal())
                .filter((portal) -> portal != null)
                .toList();
        return Collections.unmodifiableList(list);
    }

    public List<Item> wallItems() {
        List<Item> list = vistas.stream()
                .filter((v) -> v instanceof Wall)
                .flatMap((v) -> ((Wall) v).getItemsOnWall().stream())
                .toList();
        return Collections.unmodifiableList(list);
    }
    
    public List<Actor> actors() {
        return Arena.getInstance().getAllActors().stream()
                .filter((a) -> a.getLocation() == this)
                .toList();
    }

    @Override
    protected boolean doTrigger(Triggered action, Entity other) {
        return super.doTrigger(action, other);
    }

    public ActionResult clearWith(Item tool) {
        if (properties.lookup("blocked") == null) {
            return new ActionResult(Outcome.NOT_APPLICABLE);
        } else if (!isBlocked()) {
            return new ActionResult(Outcome.ALREADY_DONE);
        } else {
            setBlocked(false);
            return new ActionResult(Outcome.SUCCESS);
        }
    }

    public Exit exit(Direction direction) {
        return vistas.stream()
                .filter((v) -> v instanceof Exit)
                .map((v) -> (Exit) v)
                .filter((e) -> e.matchesDirection(direction))
                .findFirst()
                .orElse(null);
    }
 }
