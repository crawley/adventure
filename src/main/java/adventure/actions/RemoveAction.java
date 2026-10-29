package adventure.actions;

import adventure.Actor;
import adventure.Item;
import adventure.actions.Trigger.Event;

public class RemoveAction extends Verb {
    
    public RemoveAction() {
        super("remove", "removed", "removed");
    }

    @Override
    public int perform(Actor actor, String[] args) {
        return performOnItem(actor, args, Context.ITEMS_WORN);
    }

    @Override
    public String describe() {
        return "Remove an item that you are wearing";
    }

    @Override
    protected VerbResult apply(Actor actor, Item item) {
        return actor.unwearItem(item);
    }
    
    @Override
    protected void onSuccess(Item item, Item item2) {
        System.out.println("You " + getParticiple() + " " + item.getName());
        item.trigger(Event.UNWORN);
    }
}
