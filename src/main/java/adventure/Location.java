package adventure;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adventure.actions.Triggered;

public class Location extends Entity {
    
    public enum LocationType {
        ROOM("in"), PASSAGEWAY("in"), PLATFORM("on"), STAIRS("on"),
        PATH("on"), AREA1("in"), AREA2("on");
        
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
    public static final long BLOCKED = 0x4L;
        
    @SuppressWarnings("serial")
    public static final Map<String, FlagDescriptor> LOCATION_FLAGS = new HashMap<>(){
        {
            put("DARK", new FlagDescriptor (DARK));
            put("HAS_LIGHT", new FlagDescriptor (HAS_LIGHT, "light_on", false));
            put("BLOCKED", new FlagDescriptor (BLOCKED));
        }
    };
	
	private final List<Vista> vistas;
	private final EntityList<Item> items;
	private final LocationType type;

    public Location(LocationType type, String label, String name, 
            String abbrev, String description, long flags) {
		super(label, name, abbrev, description, flags, null, null, null);
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
    
    public void addVista(Vista vista) {
        vistas.add(vista);
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
        return (getFlags() & BLOCKED) != 0;
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
    protected boolean doTrigger(Triggered action, Entity target) {
        throw new UnsupportedOperationException("No location triggers");
    }
 }
