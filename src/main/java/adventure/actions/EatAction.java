package adventure.actions;

import adventure.Actor;
import adventure.Item;
import adventure.actions.Trigger.Event;

public class EatAction extends Verb {
    
    public EatAction() {
        super("eat", "eaten", "ate");
    }

	@Override
	public int perform(Actor actor, String[] args) {
		return performOnItem(actor, args, Context.HELD);
	}
	
	@Override
    public String describe() {
        return "Eat an item";
    }

    @Override
    protected VerbResult apply(Actor actor, Item item) {
        return actor.eatItem(item);
    }
    
    @Override
    protected void onSuccess(Item item, Item item2) {
        System.out.println("You " + getParticiple() + " " + item.getName());
        item.trigger(Event.CONSUMED);
        item.destroy();
    }
}
