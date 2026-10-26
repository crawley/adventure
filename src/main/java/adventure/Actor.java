package adventure;

import java.util.List;

import adventure.actions.Trigger.Event;
import adventure.actions.VerbResult;
import adventure.actions.VerbResult.Outcome;

public class Actor extends Entity {
    
    public enum Kind {
        PLAYER, NPC, PET, MONSTER
    }
    
    private final Kind kind;
    private Location currentLocation;
    private final List<Item> itemsInHand = new ItemList("in hand");
    private final List<Item> itemsWorn = new ItemList("worn");
    private final int NOS_HANDS = 2;
    
    
	public Actor(Kind kind, String label, String name, String abbrev,
	        String description, Location location) {
		super(label, name, abbrev, description);
		this.currentLocation = location;
		this.kind = kind;
	}

	public Kind getKind() {
        return kind;
    }

    public Location getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
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

    public void showLocationAndActors() {
        System.out.println("You are " + currentLocation.getType().getPreposition() 
                + " " + currentLocation.getName());
        for (Actor actor: currentLocation.actors()) {
            if (actor != this) {
                System.out.println("You see " + actor.getDescription());
            }
        }
    }
    
}
