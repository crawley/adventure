package adventure.actions;

import adventure.Entity;
import adventure.Item;
import adventure.Person;
import adventure.actions.Trigger.Event;

public class SwitchAction extends Verb {
    
    public SwitchAction() {
        super("switch", "switched", "switched");
    }

    @Override
    public int perform(Person actor, String[] args) {
        if (args.length == 2) {
            if (args[1].equals("on") || args[1].equals("off")) {
                return performOnItemArg(actor, args, 
                        Context.ITEMS_HELD, Context.ITEMS_ON_FLOOR, 
                        Context.PORTALS, Context.ITEMS_ON_WALLS);
            } else {
                System.out.println("You can only switch something " 
                        + args[0] + "'on' or 'off'?");
                return 0;
            }
        } else if (args.length == 1) {
            System.out.println("Do you want to switch the " 
                    + args[0] + "'on' or 'off'?");
            return 0;
        } else if (args.length == 0) {
            System.out.println("What do you want to switch?");
            return 0;
        } else {
            System.out.println("Don't understand.");
            return 0;
        }
    }
    
    @Override
    protected void onSuccess(Person actor, Entity item, Entity unused) {
        System.out.println("You " + getParticiple() + " " 
                + Grammar.definiteName(item) + (((Item) item).isOn() ? " on" : " off"));
        item.trigger(Event.SWITCHED);
    }
    
    @Override
    protected VerbResult applyArg(Person actor, Item item, String arg) {
        return item.switchItem(arg);
    }
    
    @Override
    public String describe() {
        return "Switch something 'on' or 'off'";
    }
}
