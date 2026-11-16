package adventure.actions;

import adventure.Entity;
import adventure.Item;
import adventure.Person;
import adventure.actions.ActionBase.Context;
import adventure.actions.Trigger.Event;

public class EatAction extends ActionBase {
    
    public EatAction() {
        super("eat", "eaten", "ate");
    }

	@Override
	public int perform(Person actor, String[] args) {
        ActionResult res;
        Item food = null;
        try {
            String[] words = parseOne(args);
            food = resolveItem(actor, words[0], 
                    Context.ITEMS_HELD);
            res = actor.eatItem(food);
            if (res.isSuccess()) {
                System.out.println("You " + getParticiple() + " " 
                        + Grammar.definiteName(food));
                food.trigger(Event.CONSUMED);
            }
        } catch (ActionException ex) {
            res = ex.getActionResult();
        }
        return res.process(actor, food, this);
	}
	
	@Override
    public String describe() {
        return "Eat some food";
    }
}
