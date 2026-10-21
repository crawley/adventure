package adventure.actions;

import adventure.Actor;
import adventure.Item;
import adventure.actions.Trigger.Event;

public class WearAction extends Verb {
    
    public WearAction() {
        super("wear", "wearing", "worn");
    }

	@Override
	public void perform(Actor actor, String[] args) {
		performOnItem(actor, args, Context.HELD);
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
    protected void onSuccess(Item item) {
        System.out.println("You " + getParticiple() + " " + item.getName());
        item.trigger(Event.WORN);
    }
}
