package adventure.actions;

import adventure.Item;
import adventure.Person;
import adventure.actions.Trigger.Event;

public class WearAction extends ActionBase {
    
    public WearAction() {
        super("wear", "wearing", "worn");
    }

	@Override
	public int perform(Person actor, String[] args) {
        ActionResult res;
        Item clothing = null;
        try {
            String[] words = parseOne(args);
            clothing = resolveItem(actor, words[0], 
                    Context.ITEMS_HELD);
            res = actor.wearItem(clothing);
            if (res.isSuccess()) {
                System.out.println("You " + getParticiple() + " " 
                        + Grammar.definiteName(clothing));
                clothing.trigger(Event.WORN, actor);
            }
        } catch (ActionException ex) {
            res = ex.getActionResult();
        }
        return res.process(actor, clothing, this);
	}
	
	@Override
    public String describe() {
        return "Wear an item of clothing, armor and so on";
    }
}
