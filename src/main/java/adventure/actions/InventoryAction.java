package adventure.actions;

import adventure.Actor;
import adventure.Entity;

public class InventoryAction implements Action {

	@Override
	public void perform(Actor actor, String[] args) {
        if (!actor.getItemsWorn().isEmpty()) {
            System.out.println("You are wearing: ");
            for (Entity item: actor.getItemsWorn()) {
                System.out.println("  " + item.getAbbrev() + ": " + item.getName());
            }
        }
	    if (actor.getItemsInHand().isEmpty()) {
            System.out.println("You are not holding anything");
        } else {
		    System.out.println("You are holding: ");
		    for (Entity item: actor.getItemsInHand()) {
		        System.out.println("  " + item.getAbbrev() + ": " + item.getName());
		    }
	    }
	}

    @Override
    public String describe() {
        return "List the items that you have on your person.";
    }
}
