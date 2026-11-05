package adventure.actions;

import adventure.Actor;
import adventure.Entity;
import adventure.Person;
import adventure.actions.Trigger.Event;

public class PetAction extends Verb {
    
    public PetAction() {
        super("pet", "petted", "petted");
    }

    @Override
    public int perform(Person actor, String[] args) {
        return performOnActor(actor, args);
    }
    
    @Override
    protected void onSuccess(Person actor, Entity creature, Entity unused) {
        System.out.println("You " + getParticiple() + " " + creature.getName());
        creature.trigger(Event.PETTED);
    }
    
    @Override
    protected VerbResult applyActor(Person actor, Actor target) {
        return actor.pet(target);
    }
    
    @Override
    public String describe() {
        return "Open an object";
    }
}
