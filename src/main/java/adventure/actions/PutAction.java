package adventure.actions;

import java.util.List;

import adventure.Container;
import adventure.Item;
import adventure.Person;
import adventure.actions.Trigger.Event;

public class PutAction extends ActionBase {
    
    public PutAction() {
        super("put", null, "put");
    }

    @Override
    public int perform(Person actor, String[] args) {
        ActionResult res;
        Item item = null;
        try {
            String[] words = parseTwo(args, "into");
            List<Item> containers = resolveItems(words[1], actor, 
                    Context.ITEMS_ON_FLOOR, Context.ITEMS_ON_WALLS, 
                    Context.ITEMS_HELD, Context.ITEMS_WORN);
            Item container = checkUnique(actor, containers, words[1]);
            if (!(container instanceof Container)) {
                throw new ActionException(Grammar.definiteName(container)
                        + " is not a container");
            }
            item = resolveItem(actor, words[0], 
                    Context.ITEMS_HELD);
            res = actor.putItemIntoContainer(item, (Container) container);
            if (res.isSuccess()) {
                System.out.println("You " + getParticiple() + " " 
                        + Grammar.definiteName(item) + " into " 
                        + Grammar.definiteName(container));
                item.trigger(Event.ADDED);
            }
        } catch (ActionException ex) {
            res = ex.getActionResult();
        }
        return res.process(actor, item, this);
    }
    
    @Override
    public String describe() {
        return "Put an item into a container";
    }

}
