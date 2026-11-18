package adventure.actions;

import adventure.Direction;
import adventure.Entity;
import adventure.Exit;
import adventure.Item;
import adventure.Location;
import adventure.Person;
import adventure.Trigger.Event;
import adventure.actions.ActionResult.Outcome;

public class ClearAction extends ActionBase {
    
    public ClearAction() {
        super("clear", "cleared", "cleared");
    }

    @Override
    public int perform(Person actor, String[] args) {
        ActionResult res;
        Entity target = null;
        try {
            String[] words = parseTwo(args, "with");
            Direction direction = parseDirection(words[0]);
            Exit exit = actor.getLocation().exit(direction);
            Item tool = resolveItem(actor, words[1], Context.ITEMS_HELD);
            if (exit == null) {
                res = new ActionResult(Outcome.NOT_POSSIBLE, 
                        "cannot " + getVerb() + " in that direction.");
            } else {
                Item portal = exit.getPortal();
                if (portal != null && portal.isBlocked()) {
                    res = portal.clearWith(tool);
                    target = portal;
                } else {
                    Location loc = exit.getTo();
                    res = loc.clearWith(tool);
                    target = loc;
                }
                if (res.isSuccess()) {
                    target.trigger(Event.CLEARED);
                    System.out.println("You " + getParticiple() + " " 
                            + Grammar.definiteName(target));
                }
            }
        } catch (ActionException ex) {
            res = ex.getActionResult();
        }
        return res.process(actor, target, this);
    }
            
    @Override
    public String describe() {
        return "Clear a path or location";
    }
}
