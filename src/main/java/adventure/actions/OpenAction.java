package adventure.actions;

import adventure.Item;
import adventure.Person;
import adventure.Trigger.Event;

public class OpenAction extends ActionBase {
    
    public OpenAction() {
        super("open", "open", "opened");
    }

    @Override
    public int perform(Person actor, String[] args) {
        ActionResult res;
        Item thing = null;
        Item tool = null;
        try {
            String[] words;
            if (args.length == 1) {
                words = parseOne(args);
            } else {
                words = parseTwo(args, "with");
            }
            thing = resolveItem(actor, words[0], 
                    Context.ITEMS_HELD, Context.ITEMS_ON_FLOOR, 
                    Context.ITEMS_ON_WALLS, Context.PORTALS);
            if (words.length > 1) {
                tool = resolveItem(actor, words[1], Context.ITEMS_HELD);
            }
            res = thing.openItemWith(tool);
            if (res.isSuccess()) {
                System.out.println("You " + getParticiple() + " " 
                        + Grammar.definiteName(thing));
                thing.trigger(Event.OPENED);
            }
        } catch (ActionException ex) {
            res = ex.getActionResult();
        }
        return res.process(actor, thing, this);
    }
        
    @Override
    public String describe() {
        return "Open an object";
    }
}
