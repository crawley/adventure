package adventure.actions;

import adventure.Actor;
import adventure.Entity;
import adventure.Item;
import adventure.actions.Trigger.Event;

public class TakeAction extends Verb {
    
    public TakeAction() {
        super("take", "held", "took");
    }

	@Override
	public int perform(Actor actor, String[] args) {
	    return performOnItem(actor, args, 
	            Context.ITEMS_ON_FLOOR, Context.ITEMS_ON_WALLS);
	}
	
	@Override
    public String describe() {
        return "Take (pick up) item(s).";
    }

    @Override
    protected VerbResult apply(Actor actor, Item item) {
        return actor.holdItem(item);
    }
    
    @Override
    protected void onSuccess(Item item, Entity item2) {
        System.out.println("You " + getParticiple() + " " + item.getName());
        item.trigger(Event.TAKEN);
    }
}
