package adventure.actions;

import adventure.Actor;
import adventure.Entity;
import adventure.Person;
import adventure.Item;

public class FeedAction extends Verb {
    
    public FeedAction() {
        super("feed", "fed", "fed");
    }

    @Override
    public int perform(Person actor, String[] args) {
        return performOnActorWith(actor, args, "with", Context.ITEMS_HELD);
    }
    
    @Override
    protected void onSuccess(Person actor, Entity creature, Entity food) {
        System.out.println("You " + getParticiple() + " " + creature.getName());
    }
    
    @Override
    protected VerbResult applyActorWith(Person actor, Actor target, Item food) {
        return actor.feed(target, food);
    }
    
    @Override
    public String describe() {
        return "Feed a creature";
    }
}
