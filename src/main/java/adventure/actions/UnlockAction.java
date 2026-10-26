package adventure.actions;

import adventure.Actor;
import adventure.Item;
import adventure.actions.Trigger.Event;

public class UnlockAction extends Verb {
    
    public UnlockAction() {
        super("unlock", "unlocked", "unlocked");
    }

    @Override
    public void perform(Actor actor, String[] args) {
        performOnItemWith(actor, args, false, "with", 
                Context.FLOOR, Context.WALLS, Context.PORTALS);
    }
    
    @Override
    protected void onSuccess(Item item, Item item2) {
        System.out.println("You " + getParticiple() + " " + item.getName());
        item.trigger(Event.UNLOCKED);
    }
    
    @Override
    protected VerbResult applyWith(Actor actor, Item lockable, Item key) {
        return lockable.unlockItem(key);
    }
    
    @Override
    public String describe() {
        return "Unlock an object";
    }

}
