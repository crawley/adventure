package adventure;

import java.util.Collections;
import java.util.List;

import adventure.actions.Trigger;
import adventure.actions.Trigger.Event;
import adventure.actions.Triggered;
import adventure.actions.VerbResult;
import adventure.actions.VerbResult.Outcome;

public class Actor extends Entity {
    
    public enum Kind {
        PLAYER, NPC, PET, MONSTER
    }
    
    private final Kind kind;
    private final List<Item> itemsInHand = new ItemList("in hand");
    private final List<Item> itemsWorn = new ItemList("worn");
    private final List<Trigger> triggers;
    private final int NOS_HANDS = 2;
    
    private Location location;
    private int hunger = 0;
    
	public Actor(Kind kind, String label, String name, String abbrev,
	        String description, Location location, List<Trigger> triggers) {
		super(label, name, abbrev, description);
		this.location = location;
		this.kind = kind;
		this.triggers = (triggers == null) ? Collections.emptyList() : triggers;
	}

	public Kind getKind() {
        return kind;
    }

    public int getHunger() {
        return hunger;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public Location getCurrentLocation() {
		return location;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.location = currentLocation;
	}

	public VerbResult wearItem(Item item) {
	    if (item.isWearable()) {
	        if (itemsWorn.contains(item)) {
	            return new VerbResult(Outcome.ALREADY_DONE, item);
	        } else {
	            itemsWorn.add(item);
	            itemsInHand.remove(item);
	            return new VerbResult(3, item);
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
        return new VerbResult(2, item); 
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
            return new VerbResult(1, item);
        } else {
            return new VerbResult(Outcome.NOT_APPLICABLE, item);
        }
    }
        
    public VerbResult dropItem(Item item) {
        itemsInHand.remove(item);
        getCurrentLocation().addItem(item);
        return new VerbResult(1, item);
    }

    public boolean handsFull() {
        return itemsInHand.size() >= NOS_HANDS;
    }

    public VerbResult eatItem(Item item) {
        if (item.isEdible()) {
            itemsInHand.remove(item);
            return new VerbResult(2, item);
        } else {
            return new VerbResult(Outcome.NOT_APPLICABLE, item);
        }
    }

    public void showLocationAndActors() {
        System.out.println("You are " + location.getType().getPreposition() 
                + " " + location.getName());
        for (Actor actor: location.actors()) {
            if (actor != this) {
                System.out.println("You see " + actor.getDescription());
            }
        }
    }
    
    public void trigger(Trigger.Event event) {
        for (Trigger trigger: triggers) {
            if (trigger.getEvents().contains(event)) {
                for (Triggered action: trigger.getActions()) {
                    switch (action.getVerb()) {
                    case DECR_AND_TEST:
                    case RESET_IF_ZERO:
                        break;
                    case SAY:
                        for (Actor actor: Arena.getInstance().getAllActors()) {
                            if (actor != this && actor.location == location) {
                                if (action.trigger()) {
                                    System.out.println(
                                            this.getName() + " says " + action.getMessage());
                                }
                            }
                        }
                        break;
                    default:
                    }
                }
            }
        }
    }

}
