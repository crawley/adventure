package adventure.actions;

import adventure.Actor;
import adventure.Entity;
import adventure.Item;
import adventure.actions.Trigger.Event;

public class MoveAction extends Verb {
    
    public MoveAction() {
        super("move", "moved", "moved");
    }

    @Override
    public int perform(Actor actor, String[] args) {
        return performOnItem(actor, args, Context.ITEMS_ON_FLOOR, Context.ITEMS_ON_WALLS);
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
    protected void onSuccess(Actor actor, Entity item, Entity unused) {
        System.out.println("You " + getParticiple() + " " + item.getName());
        item.trigger(Event.MOVED);
    }
}
