package adventure.actions;

import adventure.Entity;
import adventure.Item;
import adventure.Person;
import adventure.actions.Trigger.Event;

public class MoveAction extends Verb {
    
    public MoveAction() {
        super("move", "moved", "moved");
    }

    @Override
    public int perform(Person actor, String[] args) {
        return performOnItem(actor, args, Context.ITEMS_ON_FLOOR, Context.ITEMS_ON_WALLS);
    }

    @Override
    public String describe() {
        return "Move an object";
    }

    @Override
    public VerbResult apply(Person actor, Item item) {
        return item.moveItem();
    }

    @Override
    protected void onSuccess(Person actor, Entity item, Entity unused) {
        System.out.println("You " + getParticiple() + " " 
                + Grammar.definiteName(item));
        item.trigger(Event.MOVED);
    }
}
