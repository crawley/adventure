package adventure.actions;

import adventure.Entity;
import adventure.Item;
import adventure.Person;
import adventure.actions.Trigger.Event;

public class TakeAction extends Verb {
    
    public TakeAction() {
        super("take", "held", "took");
    }

	@Override
	public int perform(Person actor, String[] args) {
	    return performOnItem(actor, args, 
	            Context.ITEMS_ON_FLOOR, Context.ITEMS_ON_WALLS);
	}
	
	@Override
    public String describe() {
        return "Take (pick up) item(s).";
    }

    @Override
    protected VerbResult apply(Person actor, Item item) {
        return actor.holdItem(item);
    }
    
    @Override
    protected void onSuccess(Person actor, Entity item, Entity unused) {
        System.out.println("You " + getParticiple() + " " + item.getName());
        item.trigger(Event.TAKEN);
    }
}
