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
        if (!item.isTakeable()) {
            return new VerbResult(Outcome.NOT_APPLICABLE, item);
        } else if (itemsInHand.size() >= NOS_HANDS) {
            return new VerbResult(Outcome.NOT_POSSIBLE, item,
                    "your hands are full");
        } else {
            getLocation().removeItem(item);
            itemsInHand.add(item);
            return new VerbResult(1, item);
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
            if (item.isOpenable() && !item.isOpen()) {
                return new VerbResult(Outcome.NOT_POSSIBLE, item,
                        "the " + item.getAbbrev() 
                        + " needs to be opened first");
            }
            itemsInHand.remove(item);
            consume(item);
            return new VerbResult(2, item);
        } else {
            return new VerbResult(Outcome.NOT_APPLICABLE, item);
        }
    }
    
    private void consume(Item food) {
        NumberProperty calories = food.getNumberProperty("calories");
        if (calories != null) {
            NumberProperty hunger = this.getNumberProperty("hunger");
            if (hunger != null) {
                hunger.decrement(calories.floatValue());
            }
        }
        this.trigger(Event.ATE);
        food.trigger(Event.CONSUMED);
    }

    public VerbResult pet(Actor target) {
        if (target.kind != Kind.PET) {
            return new VerbResult(Outcome.NOT_APPLICABLE, target);
        } else {
            return new VerbResult(1, target);
        }
    }

    public VerbResult feed(Actor target, Item food) {
        if (food.isEdible()) {
            if (food.isOpenable() && !food.isOpen()) {
                return new VerbResult(Outcome.NOT_POSSIBLE, food,
                        "the " + food.getAbbrev() 
                        + " needs to be opened first");
            } else {
                itemsInHand.remove(food);
                target.consume(food);
                return new VerbResult(1, target, food);
            }
        } else {
            return new VerbResult(Outcome.NOT_APPLICABLE, food,
                    "is not edible");
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
            return super.doTrigger(action, target);
        }
        return true;
    }
}
