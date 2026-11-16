package adventure.actions;

import adventure.Item;
import adventure.Person;
import adventure.actions.Trigger.Event;

public class UnlockAction extends ActionBase {
    
    public UnlockAction() {
        super("unlock", "unlocked", "unlocked");
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
            res = object.unlockItem(key);
            if (res.isSuccess()) {
                System.out.println("You " + getParticiple() + " " 
                        + Grammar.definiteName(object));
                object.trigger(Event.UNLOCKED);
            }
        } catch (ActionException ex) {
            res = ex.getActionResult();
        }
        return res.process(actor, object, this);
    }
    
    @Override
    public String describe() {
        return "Unlock an object; e.g. a door or chest";
    }
}
