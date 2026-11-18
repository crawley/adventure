package adventure.actions;

import adventure.Item;
import adventure.Person;
import adventure.Trigger.Event;

public class TakeAction extends ActionBase {
    
    public TakeAction() {
        super("take", "held", "took");
    }

	@Override
	public int perform(Person actor, String[] args) {
	    ActionResult res = null;
	    Item thing = null;
	    try {
	        String[] words = parseMulti(args);
	        for (String word: words) {
	            thing = resolveItem(actor, word, 
	                    Context.ITEMS_ON_FLOOR, Context.ITEMS_ON_WALLS);
	            res = actor.holdItem(thing);
	            if (res.isSuccess()) {
	                System.out.println("You " + getParticiple() + " " 
	                        + Grammar.definiteName(thing));
	                thing.trigger(Event.TAKEN);
	            } else {
	                break;
	            }
	        }
	    } catch (ActionException ex) {
	        res = ex.getActionResult();
	    }
	    return res.process(actor, thing, this);
	}
	
	@Override
    public String describe() {
        return "Take (pick up) item(s).";
    }
}
