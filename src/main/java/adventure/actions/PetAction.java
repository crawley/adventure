package adventure.actions;

import java.util.List;

import adventure.Actor;
import adventure.Person;
import adventure.actions.Trigger.Event;

public class PetAction extends ActionBase {
    
    public PetAction() {
        super("pet", "petted", "petted");
    }

    @Override
    public int perform(Person actor, String[] args) {
        ActionResult res;
        List<Actor> creatures;
        Actor creature = null;
        try {
            String[] words = parseOne(args);
            creatures = resolveActor(actor, words[0], Context.ACTORS_HERE);
            creature = checkUnique(actor, creatures, words[0]);
            res = actor.pet(creature);
            if (res.isSuccess()) {
                System.out.println("You " + getParticiple() + " " 
                        + Grammar.definiteName(creature));
                creature.trigger(Event.PETTED);
            }
        } catch (ActionException ex) {
            res = ex.getActionResult();
        }
        return res.process(actor, creature, this);
    }
        
    @Override
    public String describe() {
        return "Pet a creature";
    }
}
