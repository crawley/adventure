package adventure;

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
    private final int NOS_HANDS = 2;
    
    private Location location;
    
	public Actor(Kind kind, String label, String name, String abbrev,
	        String description, Location location, 
	        List<Trigger> triggers, PropertySet properties) {
		super(label, name, abbrev, description, triggers, properties);
		this.location = location;
		this.kind = kind;
	}

	public Kind getKind() {
        return kind;
    }

    public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
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
            getLocation().addItem(item);
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
            getLocation().removeItem(item);
            itemsInHand.add(item);
            return new VerbResult(1, item);
        } else {
            return new VerbResult(Outcome.NOT_APPLICABLE, item);
        }
    }
        
    public VerbResult dropItem(Item item) {
        itemsInHand.remove(item);
        getLocation().addItem(item);
        return new VerbResult(1, item);
    }

    public boolean handsFull() {
        return itemsInHand.size() >= NOS_HANDS;
    }

    public VerbResult eatItem(Item item) {
        if (item.isEdible()) {
            itemsInHand.remove(item);
            this.trigger(Event.ATE);
            NumberProperty calories = item.getNumberProperty("calories");
            if (calories != null) {
                NumberProperty hunger = this.getNumberProperty("hunger");
                if (hunger != null) {
                    hunger.decrement(calories.floatValue());
                }
            }
            return new VerbResult(2, item);
        } else {
            return new VerbResult(Outcome.NOT_APPLICABLE, item);
        }
    }

    public void showLocationAndActors() {
        System.out.println("You are " + location.getType().getPreposition() 
                + " " + location.getName());
        for (Entity actor: location.actors()) {
            if (actor != this) {
                System.out.println("You see " + actor.getDescription());
            }
        }
    }
    
    @Override
    protected boolean doTrigger(Triggered action, Entity target) {
        switch (action.getVerb()) {
        case INCREMENT:
            Number by = getNumericArg2(action);
            getNumberProperty(action.getArg()).increment(by);
            break;
        case DECREMENT:
            by = getNumericArg2(action);
            getNumberProperty(action.getArg()).decrement(by);
            break;
        case TEST_MIN:
            if (!getNumberProperty(action.getArg()).testMin()) {
                return false;
            }
            break;
        case TEST_MAX:
            if (!getNumberProperty(action.getArg()).testMax()) {
                return false;
            }
            break;
        case THINK:
            if (this.getLabel().equals("self") && action.trigger()) {
                System.out.println("You think: " + action.getMessage());
            }
            break;
        case SAY:
            for (Entity actor: location.actors()) {
                if (actor != this && action.trigger()) {
                    System.out.println(this.getName() 
                            + " says: " + action.getMessage());
                }
            }
            break;
        default:
            throw new TriggerException(
                    "Verb '" + action.getVerb() 
                    + "' not implemented for actors");
        }
        return true;
    }

    public VerbResult pet(Actor target) {
        if (target.kind != Kind.PET) {
            return new VerbResult(Outcome.NOT_APPLICABLE, target);
        } else {
            return new VerbResult(1, target);
        }
    }
}
