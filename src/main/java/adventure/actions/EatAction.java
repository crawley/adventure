package adventure.actions;

import adventure.Entity;
import adventure.Person;
import adventure.Item;
import adventure.actions.Trigger.Event;

public class EatAction extends Verb {
    
    public EatAction() {
        super("eat", "eaten", "ate");
    }

	@Override
	public int perform(Person actor, String[] args) {
		return performOnItem(actor, args, Context.ITEMS_HELD);
	}
	
	@Override
    public String describe() {
        return "Eat an item";
    }

    @Override
    protected VerbResult apply(Person actor, Item item) {
        return actor.eatItem(item);
    }
    
    @Override
    protected void onSuccess(Person actor, Entity item, Entity unused) {
        System.out.println("You " + getParticiple() + " " + item.getName());
        item.trigger(Event.CONSUMED);
    }
}
