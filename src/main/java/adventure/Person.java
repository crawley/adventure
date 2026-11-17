package adventure;

import java.util.List;

import adventure.actions.ActionResult;
import adventure.actions.ActionResult.Outcome;
import adventure.actions.Grammar;
import adventure.actions.Trigger;
import adventure.actions.Trigger.Event;

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

	public ActionResult wearItem(Item item) {
	    if (item.isWearable()) {
	        if (itemsWorn.contains(item)) {
	            return new ActionResult(Outcome.ALREADY_DONE);
	        } else {
	            itemsWorn.add(item);
	            return new ActionResult(Outcome.SUCCESS, 3);
	        }
	    } else {
	        return new ActionResult(Outcome.NOT_APPLICABLE);
	    }
	}

    public ActionResult unwearItem(Item item) {
        if (itemsInHand.size() >= NOS_HANDS) {
            getLocation().addItem(item);
            item.trigger(Event.DROPPED);
        } else {
            itemsInHand.add(item);
            item.trigger(Event.TAKEN);
        }
        return new ActionResult(Outcome.SUCCESS, 2); 
    }

    public List<Item> getItemsWorn() {
        return itemsWorn;
    }
    
    public List<Item> getItemsInHand() {
        return itemsInHand;
    }

    public ActionResult holdItem(Item item) {
        if (!item.isTakeable()) {
            return new ActionResult(Outcome.NOT_APPLICABLE);
        } else if (itemsInHand.size() >= NOS_HANDS) {
            return new ActionResult(Outcome.NOT_POSSIBLE,
                    "your hands are full");
        } else {
            itemsInHand.add(item);
            return new ActionResult(Outcome.SUCCESS, 1);
        } 
    }
        
    public ActionResult dropItem(Item item) {
        getLocation().addItem(item);
        return new ActionResult(Outcome.SUCCESS, 1);
    }

    public boolean handsFull() {
        return itemsInHand.size() >= NOS_HANDS;
    }

    public ActionResult eatItem(Item item) {
        if (item.isEdible()) {
            if (item.isOpenable() && !item.isOpen()) {
                return new ActionResult(Outcome.NOT_POSSIBLE,
                        "the " + item.getAbbrev() 
                        + " needs to be opened first");
            }
            eat(item);
            return new ActionResult(Outcome.SUCCESS, 2);
        } else {
            return new ActionResult(Outcome.NOT_APPLICABLE);
        }
    }
    
    public ActionResult pet(Actor creature) {
        if (creature.getKind() != Kind.PET) {
            return new ActionResult(Outcome.NOT_APPLICABLE);
        } else {
            return new ActionResult(Outcome.SUCCESS, 1);
        }
    }

    public ActionResult feed(Actor creature, Item food) {
        if (food.isEdible()) {
            if (food.isOpenable() && !food.isOpen()) {
                return new ActionResult(Outcome.NOT_POSSIBLE,
                        "the " + food.getAbbrev() 
                        + " needs to be opened first");
            } else {
                getLocation().addItem(food);
                if (creature.eat(food)) {
                    creature.trigger(Event.ATE, food);
                    food.trigger(Event.CONSUMED);
                    return new ActionResult(Outcome.SUCCESS, 1);
                } else {
                    creature.trigger(Event.REFUSED);
                    return new ActionResult(Outcome.NOT_POSSIBLE,
                            Grammar.definiteName(creature) 
                            + " refused to eat "
                            + Grammar.definiteName(food));
                }
            }
        } else {
            return new ActionResult(Outcome.NOT_APPLICABLE,
                    "is not edible");
        }
    }

    public ActionResult takeItemFromContainer(Item item, Container container) {
        if (this.handsFull()) {
            return new ActionResult(Outcome.NOT_POSSIBLE,
                    "your hands are full");
        } if (!item.isTakeable()) {
            return new ActionResult(Outcome.NOT_APPLICABLE);
        } else {
            this.getItemsInHand().add(item);
            return new ActionResult(Outcome.SUCCESS, 1);
        }
    }
    
    public ActionResult putItemIntoContainer(Item item, Container container) {
        if (!item.isTakeable()) {
            return new ActionResult(Outcome.NOT_APPLICABLE);
        } else {
            container.addItem(item);
            return new ActionResult(Outcome.SUCCESS, 1);
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
