package adventure;

import java.util.List;

import adventure.actions.ActionResult;
import adventure.actions.Grammar;
import adventure.actions.Trigger;
import adventure.actions.Trigger.Event;
import adventure.actions.VerbResult;
import adventure.actions.VerbResult.Outcome;

public class Person extends Actor {
    
    private final List<Item> itemsInHand = new EntityList<Item>("in hand");
    private final List<Item> itemsWorn = new EntityList<Item>("worn");
    private final int NOS_HANDS = 2;
        
	public Person(Kind kind, String label, String name, String abbrev,
	        String description, long flags, Location location, Template base,
	        List<Trigger> triggers, PropertySet properties) {
		super(kind, label, name, abbrev, description, flags, location, 
		        base, triggers, properties);
	}

	public VerbResult wearItem(Item item) {
	    if (item.isWearable()) {
	        if (itemsWorn.contains(item)) {
	            return new VerbResult(Outcome.ALREADY_DONE, item);
	        } else {
	            itemsWorn.add(item);
	            return new VerbResult(3, item);
	        }
	    } else {
	        return new VerbResult(Outcome.NOT_APPLICABLE, item);
	    }
	}

    public VerbResult unwearItem(Item item) {
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
            itemsInHand.add(item);
            return new VerbResult(1, item);
        } 
    }
        
    public ActionResult dropItem(Item item) {
        getLocation().addItem(item);
        return new ActionResult(ActionResult.Outcome.SUCCESS, 1);
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
            eat(item);
            return new VerbResult(2, item);
        } else {
            return new VerbResult(Outcome.NOT_APPLICABLE, item);
        }
    }
    
    public VerbResult pet(Actor target) {
        if (target.getKind() != Kind.PET) {
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
                getLocation().addItem(food);
                if (target.eat(food)) {
                    target.trigger(Event.ATE, food);
                    food.trigger(Event.CONSUMED);
                    return new VerbResult(1, target, food);
                } else {
                    target.trigger(Event.REFUSED);
                    return new VerbResult(Outcome.NOT_POSSIBLE, target,
                            Grammar.definiteName(target) + " refused "
                            + Grammar.definiteName(target));
                }
            }
        } else {
            return new VerbResult(Outcome.NOT_APPLICABLE, food,
                    "is not edible");
        }
    }

    public void showLocationAndActors() {
        Location here = getLocation();
        if (!canSee()) {
            System.out.println("It is too dark to see where you are.");
            if (here.actors().stream().anyMatch((a) -> a != this)) {
                System.out.println("You sense something else is here.");
            }
        } else {
            System.out.println("You are " + here.getType().getPreposition() 
                    + " " + here.getName());
            for (Entity actor: here.actors()) {
                if (actor != this) {
                    System.out.println("You see " + actor.getDescription());
                }
            }
        }
    }

}
