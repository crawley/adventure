package adventure.actions;

import adventure.Entity;
import adventure.Item;
import adventure.Person;
import adventure.actions.Trigger.Event;

public class RemoveAction extends Verb {
    
    public RemoveAction() {
        super("remove", "removed", "removed");
    }

    @Override
    public int perform(Person actor, String[] args) {
        return performOnItem(actor, args, Context.ITEMS_WORN);
    }

    @Override
    public String describe() {
        return "Remove an item that you are wearing";
    }

    @Override
    protected VerbResult apply(Person actor, Item item) {
        return actor.unwearItem(item);
    }
    
    @Override
    protected void onSuccess(Person actor, Entity item, Entity unused) {
        System.out.println("You " + getParticiple() + " " 
                + Grammar.definiteName(item));
        item.trigger(Event.UNWORN, actor);
    }
}
