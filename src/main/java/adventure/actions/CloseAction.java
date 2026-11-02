package adventure.actions;

import adventure.Actor;
import adventure.Entity;
import adventure.Item;
import adventure.actions.Trigger.Event;

public class CloseAction extends Verb {
   
    public CloseAction() {
        super("close", "closed", "closed");
    }

    @Override
    public int perform(Actor actor, String[] args) {
        return performOnItem(actor, args, 
                Context.ITEMS_ON_FLOOR, Context.ITEMS_ON_WALLS, Context.PORTALS);
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
    protected void onSuccess(Actor actor, Entity item, Entity unused) {
        System.out.println("You " + getParticiple() + " " + item.getName());
        item.trigger(Event.CLOSED);
    }
}
