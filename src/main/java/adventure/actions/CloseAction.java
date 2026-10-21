package adventure.actions;

import adventure.Actor;
import adventure.Item;
import adventure.actions.Trigger.Event;

public class CloseAction extends Verb {
   
    public CloseAction() {
        super("close", "closed", "closed");
    }

    @Override
    public void perform(Actor actor, String[] args) {
        performOnItem(actor, args, Context.HERE, Context.PORTALS);
    }
    
    @Override
    public String describe() {
        return "Close an object";
    }

    @Override
    public VerbResult apply(Actor actor, Item item) {
        return item.closeItem();
    }
    
    @Override
    protected void onSuccess(Item item) {
        System.out.println("You " + getParticiple() + " " + item.getName());
        item.trigger(Event.CLOSED);
    }

}
