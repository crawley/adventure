package adventure;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Location extends Entity {
    
    public enum LocationType {
        ROOM("in"), PASSAGEWAY("in"), PLATFORM("on"), 
        PATH("on"), AREA1("in"), AREA2("on");
        
        private final String preposition;
        
        LocationType(String preposition) {
            this.preposition = preposition;
        }

        public String getPreposition() {
            return preposition;
        }
    }
	
	private final List<Vista> vistas;
	private final ItemList items;
	private final LocationType type;

    public Location(LocationType type, String label, String name, 
            String abbrev, String description) {
		super(label, name, abbrev, description);
		this.type = type;
		this.vistas = new ArrayList<>();
		this.items = new ItemList("here");
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
                .filter((a) -> a.getCurrentLocation() == this)
                .toList();
    }
 }
