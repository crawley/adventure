package adventure.actions;

import adventure.Actor;
import adventure.Entity;
import adventure.actions.Trigger.Event;

public class PetAction extends Verb {
    
    public PetAction() {
        super("pet", "petted", "petted");
    }

    @Override
    public int perform(Actor actor, String[] args) {
        return performOnActor(actor, args, Context.ACTORS_HERE);
    }
    
    @Override
    protected void onSuccess(Actor actor, Entity item) {
        System.out.println("You " + getParticiple() + " " + actor.getName());
        actor.trigger(Event.PETTED);
    }
    
    @Override
    protected VerbResult applyActor(Actor actor, Actor target) {
        return actor.pet(target);
    }
    
    @Override
    public String describe() {
        return "Open an object";
    }
}
