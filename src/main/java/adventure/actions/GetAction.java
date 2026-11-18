package adventure.actions;

import java.util.List;

import adventure.Container;
import adventure.Item;
import adventure.Person;
import adventure.Trigger.Event;

public class GetAction extends ActionBase {
    
    public GetAction() {
        super("get", null, "got");
    }

    @Override
    public int perform(Person actor, String[] args) {
        ActionResult res;
        Item item = null;
        try {
            String[] words = parseTwo(args, "from");
            List<Item> containers = resolveItems(words[1], actor, 
                    Context.ITEMS_ON_FLOOR, Context.ITEMS_ON_WALLS, 
                    Context.ITEMS_HELD, Context.ITEMS_WORN);
            Item container = checkUnique(actor, containers, words[1]);
            if (!(container instanceof Container)) {
                throw new ActionException(Grammar.definiteName(container)
                        + " is not a container");
            }
            List<Item> items = resolveItemsInContainers(words[0], actor, 
                    List.of((Container) container));
            item = checkUnique(actor, items, words[0]);
            res = actor.takeItemFromContainer(item, (Container) container);
            if (res.isSuccess()) {
                System.out.println("You " + getParticiple() + " " 
                        + Grammar.definiteName(item) + " from " 
                        + Grammar.definiteName(container));
                item.trigger(Event.REMOVED);
                item.trigger(Event.TAKEN);
            }
        } catch (ActionException ex) {
            res = ex.getActionResult();
        }
        return res.process(actor, item, this);
    }
        
    @Override
    public String describe() {
        return "Get an item from a container";
    }
}
