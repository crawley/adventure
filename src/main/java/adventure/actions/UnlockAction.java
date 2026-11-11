package adventure.actions;

import adventure.Entity;
import adventure.Item;
import adventure.Person;
import adventure.actions.Trigger.Event;

public class UnlockAction extends Verb {
    
    public UnlockAction() {
        super("unlock", "unlocked", "unlocked");
    }

    @Override
    public int perform(Person actor, String[] args) {
        return performOnItemWith(actor, args, false, "with", 
                Context.ITEMS_ON_FLOOR, Context.ITEMS_ON_WALLS, Context.PORTALS);
    }
    
    @Override
    protected void onSuccess(Person actor, Entity item, Entity unused) {
        System.out.println("You " + getParticiple() + " " 
                + Grammar.definiteName(item));
        item.trigger(Event.UNLOCKED);
    }
    
    @Override
    protected VerbResult applyWith(Person actor, Item lockable, Item key) {
        return lockable.unlockItem(key);
    }
    
    @Override
    public String describe() {
        return "Unlock an object";
    }

}
