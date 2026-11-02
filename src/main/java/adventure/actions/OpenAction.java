package adventure.actions;

import adventure.Actor;
import adventure.Entity;
import adventure.Item;
import adventure.actions.Trigger.Event;

public class OpenAction extends Verb {
    
    public OpenAction() {
        super("open", "open", "opened");
    }

    @Override
    public int perform(Actor actor, String[] args) {
        return performOnItem(actor, args, 
                Context.ITEMS_ON_FLOOR, Context.PORTALS, Context.ITEMS_ON_WALLS);
    }
    
    @Override
    protected void onSuccess(Item item, Entity item2) {
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
