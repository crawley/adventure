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
    private final PropertySet properties;
    private final int NOS_HANDS = 2;
    
    private Location location;
    private int hunger = 0;
    
	public Actor(Kind kind, String label, String name, String abbrev,
	        String description, Location location, 
	        List<Trigger> triggers, PropertySet properties) {
		super(label, name, abbrev, description);
		this.location = location;
		this.kind = kind;
		this.triggers = (triggers == null) ? Collections.emptyList() : triggers;
		this.properties = (properties == null) ? new PropertySet() : properties;
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

    public PropertySet getProperties() {
        return properties;
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
            if (!trigger.getEvents().contains(event)) {
                continue;
            }
            actions: for (Triggered action: trigger.getActions()) {
                switch (action.getVerb()) {
                case INCREMENT:
                    getNumberProperty(action.getArg()).increment();
                    break;
                case DECREMENT:
                    getNumberProperty(action.getArg()).increment();
                    break;
                case TEST_MIN:
                    if (!getNumberProperty(action.getArg()).testMin()) {
                        break actions;
                    }
                    break;
                case TEST_MAX:
                    if (!getNumberProperty(action.getArg()).testMax()) {
                        break actions;
                    }
                    break;
                case TEST:
                case SET_MAX:
                case SET_MIN:
                case SET:
                    break;
                case SAY:
                    for (Actor actor: location.actors()) {
                        if (actor != this && action.trigger()) {
                            System.out.println(this.getName() 
                                    + " says " + action.getMessage());
                        }
                    }
                    break;
                default:
                }
            }
        }
    }

    private NumberProperty getNumberProperty(String name) {
        Property prop = properties.lookup(name);
        if (prop == null) {
            throw new TriggerException(
                    "Can't find '" + name + "' property on actor '" + getLabel() + "'");
        }
        if (prop instanceof NumberProperty) {
            return (NumberProperty) prop;
        } else {
            throw new TriggerException(
                    "Property '" + name + "' on actor '" + getLabel() + "' not numeric");
        }
    }

    public VerbResult pet(Actor target) {
        if (target.kind != Kind.PET) {
            return new VerbResult(Outcome.NOT_APPLICABLE, target);
        } else {
            return new VerbResult(1, target);
        }
    }

}
