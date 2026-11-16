package adventure.actions;

import adventure.Item;
import adventure.Person;
import adventure.actions.Trigger.Event;

public class MoveAction extends ActionBase {
    
    public MoveAction() {
        super("move", "moved", "moved");
    }

    @Override
    public int perform(Person actor, String[] args) {
        ActionResult res;
        Item item = null;
        try {
            String[] words = parseOne(args);
            item = resolveItem(actor, words[0], 
                    Context.ITEMS_ON_FLOOR, Context.ITEMS_ON_WALLS);
            res = item.moveItem();
            if (res.isSuccess()) {
                System.out.println("You " + getParticiple() + " " 
                        + Grammar.definiteName(item));
                item.trigger(Event.MOVED);
            }
        } catch (ActionException ex) {
            res = ex.getActionResult();
        }
        return res.process(actor, item, this);
    }

    @Override
    public String describe() {
        return "Move an object";
    }
}
