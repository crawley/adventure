package adventure.actions;

import adventure.Actor;
import adventure.Item;
import adventure.actions.Trigger.Event;

public class RemoveAction extends Verb {
    
    public RemoveAction() {
        super("remove", "removed", "removed");
    }

    @Override
    public void perform(Actor actor, String[] args) {
        performOnItem(actor, args, Context.WORN);
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
    protected void onSuccess(Item item) {
        System.out.println("You " + getParticiple() + " " + item.getName());
        item.trigger(Event.UNWORN);
    }
}
