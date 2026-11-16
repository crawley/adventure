package adventure.actions;

import adventure.Item;
import adventure.Person;
import adventure.actions.Trigger.Event;

public class RemoveAction extends ActionBase {
    
    public RemoveAction() {
        super("remove", "removed", "removed");
    }

    @Override
    public int perform(Person actor, String[] args) {
        ActionResult res;
        Item clothing = null;
        try {
            String[] words = parseOne(args);
            clothing = resolveItem(actor, words[0], 
                    Context.ITEMS_WORN);
            res = actor.unwearItem(clothing);
            if (res.isSuccess()) {
                System.out.println("You " + getParticiple() + " " 
                        + Grammar.definiteName(clothing));
                clothing.trigger(Event.UNWORN, actor);
            }
        } catch (ActionException ex) {
            res = ex.getActionResult();
        }
        return res.process(actor, clothing, this);
    }

    @Override
    public String describe() {
        return "Remove (take off) an item of clothing, armor and so on";
    }
}
