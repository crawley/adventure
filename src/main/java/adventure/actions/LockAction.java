package adventure.actions;

import adventure.Actor;
import adventure.Item;
import adventure.actions.Trigger.Event;

public class LockAction extends Verb {
    
    public LockAction() {
        super("lock", "locked", "locked");
    }

    @Override
    public int perform(Actor actor, String[] args) {
        return performOnItemWith(actor, args, false, "with", 
                Context.ITEMS_ON_FLOOR, Context.ITEMS_ON_WALLS, Context.PORTALS);
    }
    
    
    @Override
    protected void onSuccess(Item item, Item item2) {
        System.out.println("You " + getParticiple() + " " + item.getName());
        item.trigger(Event.LOCKED);
    }
    
    @Override
    protected VerbResult applyWith(Actor actor, Item lockable, Item key) {
        return lockable.lockItem(key);
    }
    
    @Override
    public String describe() {
        return "Lock an object";
    }
}
