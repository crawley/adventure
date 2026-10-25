package adventure.actions;

import adventure.Actor;
import adventure.Direction;
import adventure.Exit;
import adventure.Item;
import adventure.Location;

public class GoAction implements Action {
	
	private final Direction direction;
	
	public GoAction(Direction direction) {
		this.direction = direction;
	}

	@Override
	public void perform(Actor actor, String[] args) {
	    for (Exit exit: actor.getCurrentLocation().getExits()) {
	        if (exit.matchesDirection(direction)) {
	            Item portal = exit.getPortal();
	            if (portal != null && !portal.isOpen()) {
	                System.out.print("You cannot go " + direction.getName() + ": ");
	                if (portal.isOpenable()) {
	                    System.out.println(portal.getName() + " is closed");
	                } else {
	                    System.out.println("your way is blocked");
	                }
	            } else {
	                Location loc = exit.getTo();
	                actor.setCurrentLocation(loc);
	                System.out.println("You are " + loc.getType().getPreposition() 
	                        + " " + loc.getName());
	            }
				return;
			}
		}
		System.out.println("You can't go " + direction.getName());
	}
	
	@Override
    public String describe() {
        return "Go " + direction.getName() + ".";
    }
}
