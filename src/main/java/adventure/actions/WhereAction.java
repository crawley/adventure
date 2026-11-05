package adventure.actions;

import adventure.Direction;
import adventure.Exit;
import adventure.Item;
import adventure.Person;

public class WhereAction implements Action {

	@Override
	public int perform(Person actor, String[] args) {
	    actor.showLocationAndActors();
		System.out.println("You can go in the following directions: ");
		for (Exit exit: actor.getLocation().getExits()) {
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
		return 0;
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
