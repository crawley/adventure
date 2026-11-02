package adventure.actions;

import adventure.Actor;
import adventure.Entity;
import adventure.Item;
import adventure.actions.Trigger.Event;

public class FeedAction extends Verb {
    
    public FeedAction() {
        super("feed", "fed", "fed");
    }

    @Override
    public int perform(Actor actor, String[] args) {
        return performOnActorWith(actor, args, "with", Context.ITEMS_HELD);
    }
    
    @Override
    protected void onSuccess(Actor actor, Entity creature, Entity food) {
        System.out.println("You " + getParticiple() + " " + creature.getName());
        creature.trigger(Event.ATE, food);
    }
    
    @Override
    protected VerbResult applyActorWith(Actor actor, Actor target, Item food) {
        return actor.feed(target, food);
    }
    
    @Override
    public String describe() {
        return "Feed a creature";
    }
}
