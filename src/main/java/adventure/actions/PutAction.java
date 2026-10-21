package adventure.actions;

import adventure.Actor;
import adventure.Container;
import adventure.Item;
import adventure.actions.Trigger.Event;
import adventure.actions.VerbResult.Outcome;

public class PutAction extends Verb {
    
    public PutAction() {
        super("put", null, "put");
    }

    @Override
    public void perform(Actor actor, String[] args) {
        performOnItemWith(actor, args, "into", Context.HELD);
    }
    
    @Override
    protected void onSuccess(Item item) {
        System.out.println("You " + getParticiple() + " " 
                + item.getName() + " into the container");
        item.trigger(Event.ADDED);
    }
    
    @Override
    protected VerbResult applyWith(Actor actor, Item item, Item container) {
        if (container instanceof Container) {
            ((Container) container).addItem(item);
            actor.getItemsInHand().remove(item);
            return new VerbResult(Outcome.SUCCESS, item);
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
