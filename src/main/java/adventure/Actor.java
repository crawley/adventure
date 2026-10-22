package adventure;

import java.util.List;

import adventure.actions.Trigger.Event;
import adventure.actions.VerbResult;
import adventure.actions.VerbResult.Outcome;

public class Actor {
    private final String name;
    private final Arena arena;
    private Location currentLocation;
    private final List<Item> itemsInHand = new ItemList("in hand");
    private final List<Item> itemsWorn = new ItemList("worn");
    private final int NOS_HANDS = 2;
    
    
	public Actor(String name, Location location, Arena arena) {
		this.name = name;
		this.currentLocation = location;
		this.arena = arena;
	}

	public Arena getArena() {
        return arena;
    }

    public Location getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}

	public String getName() {
		return name;
	}
    
	public VerbResult wearItem(Item item) {
	    if (item.isWearable()) {
	        if (itemsWorn.contains(item)) {
	            return new VerbResult(Outcome.ALREADY_DONE, item);
	        } else {
	            itemsWorn.add(item);
	            itemsInHand.remove(item);
	            return new VerbResult(Outcome.SUCCESS, item);
	        }
	    } else {
	        return new VerbResult(Outcome.NOT_APPLICABLE, item);
	    }
	}

    public VerbResult unwearItem(Item item) {
        itemsWorn.remove(item);
        if (itemsInHand.size() >= NOS_HANDS) {
            getCurrentLocation().addItem(item);
            item.trigger(Event.DROPPED);
        } else {
            itemsInHand.add(item);
            item.trigger(Event.TAKEN);
        }
        return new VerbResult(Outcome.SUCCESS, item); 
    }

    public List<Item> getItemsWorn() {
        return itemsWorn;
    }
    
    public List<Item> getItemsInHand() {
        return itemsInHand;
    }

    public VerbResult holdItem(Item item) {
        if (itemsInHand.size() >= NOS_HANDS) {
            return new VerbResult(Outcome.FULL);
        } else if (item.isTakeable()) {
            getCurrentLocation().removeItem(item);
            itemsInHand.add(item);
            return new VerbResult(Outcome.SUCCESS, item);
        } else {
            return new VerbResult(Outcome.NOT_APPLICABLE, item);
        }
    }
        
    public VerbResult dropItem(Item item) {
        itemsInHand.remove(item);
        getCurrentLocation().addItem(item);
        return new VerbResult(Outcome.SUCCESS, item);
    }

    public boolean handsFull() {
        return itemsInHand.size() >= NOS_HANDS;
    }

    public VerbResult eatItem(Item item) {
        if (item.isEdible()) {
            itemsInHand.remove(item);
            return new VerbResult(Outcome.SUCCESS, item);
        } else {
            return new VerbResult(Outcome.NOT_APPLICABLE, item);
        }
    }
    
}
