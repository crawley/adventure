package adventure.actions;

import java.util.Arrays;
import java.util.List;

import adventure.Direction;
import adventure.Entity;
import adventure.Exit;
import adventure.Item;
import adventure.Location;
import adventure.Person;
import adventure.Vista;
import adventure.Wall;

public class LookAction implements Action {

	@Override
	public int perform(Person actor, String[] args) {
        Location here = actor.getLocation();
	    if (args.length == 0) {
	        actor.showLocationAndActors();
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
	        System.out.println("This is " + Grammar.indefiniteDescription(here));
	    } else {
	        Direction[] directions;
	        
	        if (args[0].equals("all")) {
	            directions = new Direction[] {
	                    Direction.n, Direction.ne, Direction.e, Direction.se, 
	                    Direction.s, Direction.sw, Direction.w, Direction.nw, 
	                    Direction.a, Direction.b}; 
	        } else {
	            try {
	               directions = Arrays.stream(args)
	                    .map((s) -> directionForName(s))
	                    .toArray(Direction[]::new);
	            } catch (IllegalArgumentException ex) {
	                System.out.println("Unknown direction: " + ex.getMessage());
	                return 0;
	            }
	        }
	        boolean seen = false;
	        for (Direction direction: directions) {
	            seen |= lookInDirection(here, direction);
	        }
	        if (!seen) {
	            if (args[0].equals("all")) {
	                System.out.println("There is nothing to see here.  Move along.");
	            } else {
	                System.out.print("There is nothing to see ");
	                if (args.length == 1) {
	                    System.out.println("in that direction.");
	                } else {
                        System.out.println("in those directions.");
	                }
	            }
	        }
	    }
		return 0;
	}
	
	private Direction directionForName(String name) {
	    name = name.toLowerCase();
	    for (Direction dir: Direction.values()) {
	        if (dir.toString().equals(name) ||
	                dir.getName().equals(name)) {
	            return dir;
	        }
	    }
	    throw new IllegalArgumentException(name);
	}

    private boolean lookInDirection(Location here, Direction direction) {
        List<Vista> vistas = here.getVistas().stream()
                .filter((w) -> w.mainDirection().equals(direction))
                .toList();
        if (!vistas.isEmpty()) {
            System.out.print(describeDirection(direction) + " you see ");
            Wall wall = null;
            for (Vista vista: vistas) {
                if (vista instanceof Exit) {
                    describeExit((Exit) vista, wall);
                } else if (vista instanceof Wall) {
                    wall = (Wall) vista;
                    describeWall(wall);
                } else {
                    describeVista(vista);
                }
            }
            return true;
        } else {
            return false;
        }
    }
	
    private String describeDirection(Direction direction) {
        return switch(direction) {
        case a -> "Above you";
        case b -> "Below you";
        default -> "To the " + direction.getName();
        };
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
            for (Entity item: items) {
                System.out.println("   " + item.getAbbrev() 
                + ": " + item.getName());
            }
        } else {
            System.out.println();
        }
    }

    private void describeExit(Exit exit, Wall wall) {
        String desc;
        Item portal = exit.getPortal();
        if (wall != null) {
            System.out.print("In the wall there is ");
            if (portal != null) {
                desc = "a " + portal.getAbbrev();
            } else {
                desc = "an exit";
            }
            if (portal == null || !portal.isOpenable() || portal.isOpen()) {
                desc += " to " + exit.getTo().getName();
            }
        } else {
            desc = exit.getDescription();
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
