package adventure.actions;

import adventure.Actor;
import adventure.Exit;
import adventure.Item;
import adventure.Location;

public class WhereAction implements Action {

	@Override
	public void perform(Actor actor, String[] args) {
	    Location here = actor.getCurrentLocation();
		System.out.println("You are in " + here.getName());
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
        return "Print navigation info for the current location.";
    }
}
