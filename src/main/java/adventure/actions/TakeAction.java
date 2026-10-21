package adventure.actions;

import adventure.Actor;
import adventure.Item;
import adventure.actions.Trigger.Event;

public class TakeAction extends Verb {
    
    public TakeAction() {
        super("take", "held", "took");
    }

	@Override
	public void perform(Actor actor, String[] args) {
	    performOnItem(actor, args, Context.FLOOR);
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
    protected void onSuccess(Item item, Item item2) {
        System.out.println("You " + getParticiple() + " " + item.getName());
        item.trigger(Event.TAKEN);
    }
}
