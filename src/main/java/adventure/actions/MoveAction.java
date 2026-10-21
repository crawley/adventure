package adventure.actions;

import adventure.Actor;
import adventure.Item;
import adventure.actions.Trigger.Event;

public class MoveAction extends Verb {
    
    public MoveAction() {
        super("move", "moved", "moved");
    }

    @Override
    public void perform(Actor actor, String[] args) {
        performOnItem(actor, args, Context.FLOOR);
    }

    @Override
    public String describe() {
        return "Move an object";
    }

    @Override
    public VerbResult apply(Actor actor, Item item) {
        return item.moveItem();
    }

    @Override
    protected void onSuccess(Item item, Item item2) {
        System.out.println("You " + getParticiple() + " " + item.getName());
        item.trigger(Event.MOVED);
    }
}
