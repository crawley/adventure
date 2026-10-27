package adventure.actions;

import adventure.Actor;
import adventure.Item;
import adventure.actions.Trigger.Event;

public class DropAction extends Verb {
    
    public DropAction() {
        super("drop", "dropped", "dropped");
    }

	@Override
	public int perform(Actor actor, String[] args) {
	    return performOnItem(actor, args, Context.HELD);
	}
	
	@Override
    public VerbResult apply(Actor actor, Item item) {
        return actor.dropItem(item);
    }
	
    @Override
    protected void onSuccess(Item item, Item item2) {
        System.out.println("You " + getParticiple() + " " + item.getName());
        item.trigger(Event.DROPPED);
    }

    @Override
    public String describe() {
        return "drop an item or items";
    }
}
