package adventure.actions;

import adventure.Item;
import adventure.Person;
import adventure.actions.Trigger.Event;

public class SwitchAction extends ActionBase {
    
    public SwitchAction() {
        super("switch", "switched", "switched");
    }

    @Override
    public int perform(Person actor, String[] args) {
        ActionResult res;
        Item thing = null;
        try {
            String words[] = parseTwo(args, "to");
            thing = resolveItem(actor, words[0], 
                    Context.ITEMS_HELD, Context.ITEMS_WORN, 
                    Context.ITEMS_ON_FLOOR, Context.PORTALS, 
                    Context.ITEMS_ON_WALLS);
            boolean on = switch(words[1]) {
            case "on" -> true;
            case "off" -> false;
            default ->
                throw new ActionException("'" + words[1] 
                        + "' should be 'on' or 'off'");
            };
            res = thing.switchItem(on);
            if (res.isSuccess()) {
                System.out.println("You " + getParticiple() + " " 
                        + Grammar.definiteName(thing) + " " + words[1]);
                thing.trigger(Event.SWITCHED);
            }
        } catch (ActionException ex) {
            res = ex.getActionResult();
        }
        return res.process(actor, thing, this);
    }
    
    @Override
    public String describe() {
        return "Switch something 'on' or 'off'";
    }
}
