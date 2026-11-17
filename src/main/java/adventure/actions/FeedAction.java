package adventure.actions;

import java.util.List;

import adventure.Actor;
import adventure.Item;
import adventure.Person;

public class FeedAction extends ActionBase {
    
    public FeedAction() {
        super("feed", "fed", "fed");
    }

    @Override
    public int perform(Person actor, String[] args) {
        ActionResult res;
        Actor creature = null;
        try {
            String[] words = parseTwo(args, "to");
            List<Actor> creatures = resolveActor(actor, words[1], 
                    Context.ACTORS_HERE);
            creature = checkUnique(actor, creatures, words[1]);
            List<Item> items = resolveItems(words[0], actor, 
                    Context.ITEMS_HELD);
            Item food = checkUnique(actor, items, words[0]);
            res = actor.feed(creature, food);
            if (res.isSuccess()) {
                System.out.println("You " + getParticiple() + " " 
                        + Grammar.definiteName(creature));
            }
        } catch (ActionException ex) {
            res = ex.getActionResult();
        }
        return res.process(actor, creature, this);
    }
    
    public String describe() {
        return "Feed a creature";
    }
}
