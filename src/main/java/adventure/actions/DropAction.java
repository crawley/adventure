package adventure.actions;

import adventure.Entity;
import adventure.Item;
import adventure.Person;
import adventure.actions.Trigger.Event;

public class DropAction extends Verb {
    
    public DropAction() {
        super("drop", "dropped", "dropped");
    }

	@Override
	public int perform(Person actor, String[] args) {
	    return performOnItem(actor, args, Context.ITEMS_HELD);
	}
	
	@Override
    public VerbResult apply(Person actor, Item item) {
        return actor.dropItem(item);
    }
	
	
    @Override
    protected void onSuccess(Person actor, Entity item, Entity unused) {
        System.out.println("You " + getParticiple() + " " + item.getName());
        item.trigger(Event.DROPPED);
    }

    @Override
    public String describe() {
        return "drop an item or items";
    }
}
