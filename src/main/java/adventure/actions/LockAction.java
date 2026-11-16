package adventure.actions;

import adventure.Item;
import adventure.Person;
import adventure.actions.Trigger.Event;

public class LockAction extends ActionBase {
    
    public LockAction() {
        super("lock", "locked", "locked");
    }

    @Override
    public int perform(Person actor, String[] args) {
        ActionResult res;
        Item object = null;
        try {
            String[] words = parseTwo(args, "with");
            object = resolveItem(actor, words[0], 
                    Context.ITEMS_ON_FLOOR, Context.ITEMS_ON_WALLS, 
                    Context.PORTALS);
            Item key = resolveItem(actor, words[1], Context.ITEMS_HELD);
            res = object.lockItem(key);
            if (res.isSuccess()) {
                System.out.println("You " + getParticiple() + " " 
                        + Grammar.definiteName(object));
                object.trigger(Event.LOCKED);
            }
        } catch (ActionException ex) {
            res = ex.getActionResult();
        }
        return res.process(actor, object, this);
    }
    
    @Override
    public String describe() {
        return "Lock an object; e.g. a door or chest";
    }
}
