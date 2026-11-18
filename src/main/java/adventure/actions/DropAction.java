package adventure.actions;

import java.util.List;

import adventure.Item;
import adventure.Person;
import adventure.Trigger.Event;

public class DropAction extends ActionBase {
    
    public DropAction() {
        super("drop", "dropped", "dropped");
    }

	@Override
	public int perform(Person actor, String[] args) {
        ActionResult res = null;
        int timeTaken = 0;
	    try {
	        String[] words = parseMulti(args);
	        loop: for (String word: words) {
	            List<Item> items = ActionBase.resolveItems(word, actor,
	                    Context.ITEMS_HELD);
	            checkNonempty(actor, items, word);
	            for (Item item: items) {
	                res = actor.dropItem(item);
	                if (!res.isSuccess()) {
	                    break loop;
	                }
	                timeTaken += res.getTimeTaken();
	                System.out.println("You " + getParticiple() + " " 
	                        + Grammar.indefiniteName(item));
	                item.trigger(Event.DROPPED);
	            }
	        }
	    } catch (ActionException ex) {
	        res = ex.getActionResult();
	    }
	    return timeTaken + (res == null ? 0 : res.process(actor, null, this));
	}

    @Override
    public String describe() {
        return "drop an item or items";
    }
}
