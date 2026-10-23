package adventure.actions;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import adventure.Actor;
import adventure.Direction;
import adventure.Exit;
import adventure.Item;
import adventure.Location;
import adventure.Room;
import adventure.Wall;

public class LookAction implements Action {

	@Override
	public void perform(Actor actor, String[] args) {
	    Location here = actor.getCurrentLocation();
		if (here.getDescription() == null) {
		    System.out.println("You are in " + here.getName());
		} else {
		    System.out.println(here.getDescription());
		}
		List<Item> items = here.getItems();
		boolean firstItem = true;
		for (Item item: items) {
		    if (!item.isHidden()) {
		        if (firstItem) {
		            System.out.println("You can see the following items: ");
		            firstItem = false;
		        }
		        System.out.println(
		                "  " + item.getAbbrev() + ": " + item.getName());
		    }
		}
		List<Wall> walls = (here instanceof Room) ? ((Room) here).getWalls() : Collections.emptyList();
		List<Exit> exits = (here.getExits() != null) ? here.getExits() : Collections.emptyList();
		for (Direction direction: Direction.values()) {
		    Optional<Wall> optWall = walls.stream()
		            .filter((w) -> w.getDirection().equals(direction))
		            .findFirst();
		    Optional<Exit> optExit = exits.stream()
                    .filter((w) -> w.getDirection().equals(direction))
                    .findFirst();
		    if (optWall.isPresent() || optExit.isPresent()) {
		        System.out.print("In the " + direction.getName() + " direction you see ");
		        if (optWall.isPresent()) {
		            String desc = optWall.get().getDescription();
		            System.out.print((desc == null) ? "a wall" : desc);
		            if (optExit.isPresent()) {
		                System.out.print(" with ");
		            }
		        }
		        if (optExit.isPresent()) {
                    Exit exit = optExit.get();
                    String desc = exit.getDescription();
                    Item portal = exit.getPortal();
                    if (desc != null) {
                        System.out.print(desc);
                    } else if (portal != null) {
                        System.out.print(portal.getName());
                    } else if (here instanceof Room) {
                        System.out.print("an exit");
                    } else {
                        System.out.print(exit.getTo().getName());
                    }
                }
		        System.out.println();
		    }
		}
	}

    @Override
    public String describe() {
        return "Describe the current location.";
    }
}
