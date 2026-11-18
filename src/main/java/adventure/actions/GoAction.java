package adventure.actions;

import adventure.Direction;
import adventure.Exit;
import adventure.Item;
import adventure.Location;
import adventure.Person;

public class GoAction implements Action {
	
	private final Direction direction;
	
	public GoAction(Direction direction) {
		this.direction = direction;
	}

	@Override
	public int perform(Person actor, String[] args) {
	    Location here = actor.getLocation();
	    for (Exit exit: actor.getLocation().getExits()) {
	        if (exit.matchesDirection(direction)) {
	            Item portal = exit.getPortal();
	            if (portal != null && (portal.isClosed() || portal.isBlocked())) {
	                if (!actor.canSee()) {
	                    System.out.println("You bump into something.");
	                } else {
	                    System.out.print("You cannot go " + direction.getName() + ": ");
	                    if (portal.isClosed()) {
	                        System.out.println("the " + portal.getAbbrev() + " is closed");
	                    } else {
	                        System.out.println("your way is blocked");
	                    }
	                }
	            } else {
	                Location loc = exit.getTo();
	                if (loc.isBlocked()) {
	                    System.out.print("You cannot enter " + Grammar.definiteName(loc)
	                            + ": access to that area is blocked.");
	                } else {
	                    actor.setLocation(loc);
	                    if (loc.isDark() && !actor.hasWorkingLight()) {
	                        System.out.println("It is too dark to see where you are.");
	                    } else {
	                        System.out.println("You are " + loc.getType().getPreposition() 
	                                + " " + loc.getName() + ".");
	                    }
	                }
	            }
				return 1;
			}
		}
	    if (here.isDark() && !actor.hasWorkingLight()) {
	        System.out.println("You bump into something.");
	    } else {
		    System.out.println("You can't go " + direction.getName());
	    }
		return 0;
	}
	
	@Override
    public String describe() {
        return "Go " + direction.getName() + ".";
    }
}
