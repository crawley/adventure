package adventure.actions;

import adventure.Entity;
import adventure.Item;
import adventure.Person;
import adventure.actions.Trigger.Event;

public class OpenAction extends Verb {
    
    public OpenAction() {
        super("open", "open", "opened");
    }

    @Override
    public int perform(Person actor, String[] args) {
        if (args.length == 1) {
            return performOnItem(actor, args, 
                    Context.ITEMS_HELD, Context.ITEMS_ON_FLOOR, Context.PORTALS, 
                    Context.ITEMS_ON_WALLS);
        } else {
            return performOnItemWith(actor, args, false, "with",
                    Context.ITEMS_HELD, Context.ITEMS_ON_FLOOR, Context.PORTALS, 
                    Context.ITEMS_ON_WALLS);

        }
    }
    
    @Override
    protected void onSuccess(Person actor, Entity item, Entity unused) {
        System.out.println("You " + getParticiple() + " " + item.getName());
        item.trigger(Event.OPENED);
    }
    
    @Override
    protected VerbResult apply(Person actor, Item item) {
        return item.openItem();
    }
    
    @Override
    protected VerbResult applyWith(Person actor, Item item, Item opener) {
        return item.openItemWith(opener);
    }

    
    @Override
    public String describe() {
        return "Open an object";
    }
}
