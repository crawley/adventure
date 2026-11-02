package adventure.actions;

import adventure.Actor;
import adventure.Container;
import adventure.Entity;
import adventure.Item;
import adventure.actions.Trigger.Event;
import adventure.actions.VerbResult.Outcome;

public class PutAction extends Verb {
    
    public PutAction() {
        super("put", null, "put");
    }

    @Override
    public int perform(Actor actor, String[] args) {
        return performOnItemWith(actor, args, false, "into", Context.ITEMS_HELD);
    }
    
    @Override
    protected void onSuccess(Actor actor, Entity item, Entity item2) {
        System.out.println("You " + getParticiple() + " " 
                + item.getName() + " into " + item2.getName());
        item.trigger(Event.ADDED);
    }
    
    @Override
    protected VerbResult applyWith(Actor actor, Item item, Item container) {
        if (container instanceof Container) {
            ((Container) container).addItem(item);
            actor.getItemsInHand().remove(item);
            return new VerbResult(1, item, container);
        } else {
            return new VerbResult(Outcome.NOT_POSSIBLE, container,
                    "is not a container");
        }
    }
    
    @Override
    public String describe() {
        return "Put an item into a container";
    }

}
