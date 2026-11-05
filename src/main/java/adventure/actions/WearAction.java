package adventure.actions;

import adventure.Entity;
import adventure.Person;
import adventure.Item;
import adventure.actions.Trigger.Event;

public class WearAction extends Verb {
    
    public WearAction() {
        super("wear", "wearing", "worn");
    }

	@Override
	public int perform(Person actor, String[] args) {
		return performOnItem(actor, args, Context.ITEMS_HELD);
	}
	
	@Override
    public String describe() {
        return "Wear an item or items";
    }

    @Override
    protected VerbResult apply(Person actor, Item item) {
        return actor.wearItem(item);
    }
    
    @Override
    protected void onSuccess(Person actor, Entity item, Entity unused) {
        System.out.println("You are now " + getAdjective() + " " + item.getName());
        item.trigger(Event.WORN);
    }
}
