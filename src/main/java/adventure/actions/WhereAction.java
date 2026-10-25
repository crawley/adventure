package adventure.actions;

import adventure.Actor;
import adventure.Direction;
import adventure.Exit;
import adventure.Item;
import adventure.Location;

public class WhereAction implements Action {

	@Override
	public void perform(Actor actor, String[] args) {
	    Location here = actor.getCurrentLocation();
		System.out.println("You are " + here.getType().getPreposition() 
		        + " " + here.getName());
		System.out.println("You can go in the following directions: ");
		for (Exit exit: here.getExits()) {
			System.out.print("  " + showDirections(exit));
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

    private String showDirections(Exit exit) {
        String res = "";
        for (Direction direction: exit.getDirections()) {
            if (res.length() > 0) {
                res += ", ";
            }
            res += direction.getName();
        }
        return res;
    }

    @Override
    public String describe() {
        return "Print navigation info for the current location.";
    }
}
