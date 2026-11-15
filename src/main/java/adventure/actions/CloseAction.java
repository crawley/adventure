package adventure.actions;

import adventure.Item;
import adventure.Person;
import adventure.actions.Trigger.Event;

public class CloseAction extends ActionBase {
   
    public CloseAction() {
        super("close", "closed", "closed");
    }

    @Override
    public int perform(Person actor, String[] args) {
        ActionResult res;
        Item portal = null;
        try {
            String[] words = parse1(args);
            portal = resolveItem(actor, words[0], 
                    Context.ITEMS_ON_FLOOR, Context.ITEMS_ON_WALLS, Context.PORTALS);
            res = portal.closeItem();
            if (res.isSuccess()) {
                System.out.println("You " + getParticiple() + " " 
                        + Grammar.definiteName(portal));
                portal.trigger(Event.CLOSED);
            }
        } catch (SyntaxException ex) {
            res = ex.getActionResult();
        }
        return res.process(actor, portal, this);
    }
    
    @Override
    public String describe() {
        return "Close an object";
    }
}
