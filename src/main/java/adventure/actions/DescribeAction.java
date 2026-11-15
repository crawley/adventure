package adventure.actions;

import java.util.List;

import adventure.Container;
import adventure.Entity;
import adventure.Item;
import adventure.Person;

public class DescribeAction extends ActionBase {
    
	public DescribeAction() {
        super("describe", "described", "described");
    }

    @Override
	public int perform(Person actor, String[] args) {
	    if (args.length == 0) {
	        System.out.println("What do you want to be described?");
	    } else if (args.length > 1) {
            System.out.println("One thing at a time!");
        } else {
            String word = args[0];
            List<Item> items = ActionBase.resolveItems(
                    word, actor, Context.ITEMS_ON_FLOOR, Context.PORTALS, 
                    Context.ITEMS_ON_WALLS, Context.ITEMS_HELD, Context.ITEMS_WORN);
            if (!actor.canSee()) {
                System.out.println("You cannot see anything!");
            } else if (items.isEmpty()) {
                System.out.println("You cannot see a " + word);
            } else if (items.size() == 1) {
                describe(items.get(0), word);
            } else {
                int ordinal = 1;
                for (Item item: items) {
                    describe(item, word + "#" + ordinal);
                    ordinal++;
                }
            }
        }
	    return 0;
	}

	private void describe(Item item, String word) {
	    System.out.println(word + ": " + item.getName());
	    if (item.getDescription() != null) {
	        System.out.println("This is " + 
	                Grammar.indefiniteDescription(item));
	    }
	    if (item.hasStateFlags()) {
	        if (item.isOpenable()) {
	            System.out.print("  It is currently " + 
	                    (item.isOpen() ? "open" : "closed"));
	            if (!item.isOpen() && item.isLockable()) {
	                System.out.print(" and " +
	                    (item.isLocked() ? "locked" : "unlocked"));
	            }
	            System.out.println(".");
	        }
	        if (item.isSwitchable()) {
                System.out.println("  It is currently switched " + 
                        (item.isOn() ? "on." : "off."));
            }
	    }
	    if (item instanceof Container) {
	        if (item.isOpenable() && !item.isOpen()) {
	            System.out.println(
	                    "  You cannot see what is in " + 
	                            Grammar.definiteName(item));
	        } else {
	            List<Item> contents = ((Container) item).getContents();
	            if (contents.isEmpty()) {
	                System.out.println(
	                        "  " + Grammar.definiteName(item) 
	                        + " is empty");
	            } else {
	                System.out.println(
	                        "  " + Grammar.definiteName(item) 
	                        + " holds:");
	                for (Entity contentItem: contents) {
	                    System.out.println(
	                            "    " + contentItem.getAbbrev() 
	                            + ": " + contentItem.getName());
	                }
	            }
	        }
	    }
	}

    @Override
    public String describe() {
        return "Describe an item you are holding or wearing, or something in the current location.";
    }
}
