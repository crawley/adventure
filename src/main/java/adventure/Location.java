package adventure;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Location extends Entity {
	
	private final List<Exit> exits;
	private final ItemList items;

    public Location(String label, String name, String abbrev, String description) {
		super(label, name, abbrev, description);
		this.exits = new ArrayList<>();
		this.items = new ItemList("here");
	}

	public List<Exit> getExits() {
		return exits;
	}
	
	public void addExit(Exit exit) {
		exits.add(exit);
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
            throw new EntityException("Entity with label '" + label + "' is not a location");
        }
    }

    public List<Item> portalItems() {
        List<Item> list = exits.stream()
                .map((exit) -> exit.getPortal())
                .filter((portal) -> portal != null)
                .toList();
        return Collections.unmodifiableList(list);
    }
}
