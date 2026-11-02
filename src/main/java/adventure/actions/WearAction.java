package adventure.actions;

import adventure.Actor;
import adventure.Entity;
import adventure.Item;
import adventure.actions.Trigger.Event;

public class WearAction extends Verb {
    
    public WearAction() {
        super("wear", "wearing", "worn");
    }

	@Override
	public int perform(Actor actor, String[] args) {
		return performOnItem(actor, args, Context.ITEMS_HELD);
	}
	
	@Override
    public String describe() {
        return "Wear an item or items";
    }

    @Override
    protected VerbResult apply(Actor actor, Item item) {
        return actor.wearItem(item);
    }
    
    @Override
    protected void onSuccess(Item item, Entity item2) {
        System.out.println("You are now " + getAdjective() + " " + item.getName());
        item.trigger(Event.WORN);
    }
}
