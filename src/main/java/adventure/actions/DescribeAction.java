package adventure.actions;

import java.util.List;

import adventure.Actor;
import adventure.Container;
import adventure.Item;
import adventure.actions.Verb.Context;

public class DescribeAction implements Action {

	@Override
	public int perform(Actor actor, String[] args) {
	    if (args.length == 0) {
	        System.out.println("What do you want to be described?");
	    } else if (args.length > 1) {
            System.out.println("One thing at a time!");
        } else {
            String word = args[0];
            List<Item> items = Verb.resolve(
                    word, actor, Context.FLOOR, Context.PORTALS, 
                    Context.WALLS, Context.HELD, Context.WORN);
            if (items.isEmpty()) {
                System.out.println("Cannot see a " + word);
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
	        System.out.println("This is " + item.getDescription());
	    }
	    if (item.hasStateFlags()) {
	        if (item.isOpenable()) {
	            System.out.println("  It is " + 
	                    (item.isOpen() ? "open" : "closed"));
	        }
	        if (item.isLockable()) {
	            System.out.println("  It is " + 
	                    (item.isLocked() ? "locked" : "unlocked"));
	        }
	    }
	    if (item instanceof Container) {
	        if (item.isOpenable() && !item.isOpen()) {
	            System.out.println(
	                    "  You cannot see what is in " + item.getName());
	        } else {
	            List<Item> contents = ((Container) item).getContents();
	            if (contents.isEmpty()) {
	                System.out.println(
	                        "  " + item.getName() + " is empty");
	            } else {
	                System.out.println(
	                        "  " + item.getName() + " contains:");
	                for (Item contentItem: contents) {
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
