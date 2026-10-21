package adventure.actions;

import java.util.List;

import adventure.Actor;
import adventure.Exit;
import adventure.Item;
import adventure.Location;

public class WhereAction implements Action {

	@Override
	public void perform(Actor actor, String[] args) {
	    Location here = actor.getCurrentLocation();
		System.out.println("You are in " + here.getName());
		List<Item> items = here.getItems();
		boolean firstItem = true;
		for (Item item: items) {
		    if (!item.isHidden()) {
		        if (firstItem) {
		            System.out.println("You can see the following things: ");
		            firstItem = false;
		        }
		        System.out.println(
		                "  " + item.getAbbrev() + ": " + item.getName());
		    }
		}
		System.out.println("You can go in the following directions: ");
		for (Exit exit: here.getExits()) {
			System.out.print("  " + exit.getDirection().getName());
			Item portal = exit.getPortal();
			if (portal != null && !portal.isHidden()) {
			    System.out.print(
			            " via " + portal.getLabel() + ": " + portal.getName());
			    if (portal.isOpenable()) {
			        System.out.print(portal.isOpen() ? " (open)" : " (closed)");
			    }
			}
			System.out.println();
		}
	}

    @Override
    public String describe() {
        return "Describe your current location.";
    }
}
