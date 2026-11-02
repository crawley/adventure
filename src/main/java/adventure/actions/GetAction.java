package adventure.actions;

import adventure.Actor;
import adventure.Container;
import adventure.Entity;
import adventure.Item;
import adventure.actions.Trigger.Event;
import adventure.actions.VerbResult.Outcome;

public class GetAction extends Verb {
    
    public GetAction() {
        super("get", null, "got");
    }

    @Override
    public int perform(Actor actor, String[] args) {
        return performOnItemWith(actor, args, true, "from", 
                Context.ITEMS_HELD, Context.ITEMS_ON_FLOOR, Context.ITEMS_ON_WALLS);
    }
    
    @Override
    protected void onSuccess(Actor actor, Entity item, Entity entity) {
        System.out.println("You " + getParticiple() + " " 
                + item.getName() + " from " + entity.getName());
        item.trigger(Event.REMOVED);
        item.trigger(Event.TAKEN);
    }

    @Override
    protected VerbResult applyWith(Actor actor, Item item, Item container) {
        if (container instanceof Container) {
            ((Container) container).removeItem(item);
            actor.getItemsInHand().add(item);
            return new VerbResult(Outcome.SUCCESS, item, container);
        } else {
            return new VerbResult(Outcome.NOT_POSSIBLE, container,
                    "is not a container");
        }
    }
    
    @Override
    public String describe() {
        return "Get an item from a container";
    }

}
