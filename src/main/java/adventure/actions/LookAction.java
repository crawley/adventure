package adventure.actions;

import java.util.List;
import java.util.Optional;

import adventure.Actor;
import adventure.Direction;
import adventure.Exit;
import adventure.Item;
import adventure.Location;
import adventure.Vista;
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
		List<Vista> vistas = here.getVistas();
		for (Direction direction: Direction.values()) {
		    Optional<Vista> optVista = vistas.stream()
		            .filter((w) -> w.getDirection().equals(direction))
		            .findFirst();
		    if (optVista.isPresent()) {
		        System.out.print("In the " + direction.getName() + " direction you see ");
		        Vista vista = optVista.get();
		        if (vista instanceof Exit) {
		            describeExit((Exit) vista);
		        } else if (vista instanceof Wall) {
		            describeWall((Wall) vista);
		        } else {
		            describeVista(vista);
		        }
		    }
		}
	}

    private void describeVista(Vista vista) {
        String desc = vista.getDescription();
        if (desc == null) {
            desc = "an endless vista";
        }
        System.out.println(desc);
    }

    private void describeWall(Wall wall) {
        String desc = wall.getDescription();
        if (desc == null) {
            desc = wall.getName();
        } if (desc == null) {
            desc = "a wall";
        } 
        System.out.print(desc);
        List<Item> items = wall.getItemsOnWall();
        if (!items.isEmpty()) {
            System.out.println(". On the wall are the following:");
            for (Item item: items) {
                System.out.println("   " + item.getAbbrev() 
                + ": " + item.getName());
            }
        } else {
            System.out.println();
        }
    }

    private void describeExit(Exit exit) {
        String desc = exit.getDescription();
        Item portal = exit.getPortal();
        if (desc == null) {
            if (portal != null) {
                if (portal.getDescription() != null) {
                    desc = portal.getDescription();
                } else {
                    desc = portal.getName();
                }
            } else {
                desc = exit.getTo().getName();
            }
        }
        System.out.print(desc);
        if (portal != null && portal.isOpenable()) {
            System.out.print(". The " + portal.getAbbrev() 
                    + (portal.isOpen() ? " is open" : " is closed"));
        }
        System.out.println();
    }

    @Override
    public String describe() {
        return "Describe the current location.";
    }
}
