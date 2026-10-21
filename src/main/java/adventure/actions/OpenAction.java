package adventure.actions;

import adventure.Actor;
import adventure.Item;
import adventure.actions.Trigger.Event;

public class OpenAction extends Verb {
    
    public OpenAction() {
        super("open", "open", "opened");
    }

    @Override
    public void perform(Actor actor, String[] args) {
        performOnItem(actor, args, Context.HERE, Context.PORTALS);
    }
    
    @Override
    protected void onSuccess(Item item) {
        System.out.println("You " + getParticiple() + " " + item.getName());
        item.trigger(Event.OPENED);
    }
    
    @Override
    protected VerbResult apply(Actor actor, Item item) {
        return item.openItem();
    }
    
    @Override
    public String describe() {
        return "Open an object";
    }
}
