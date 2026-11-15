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
	        if (!actor.canSee()) {
	            System.out.println("It is too dark to see anything around you.");
	            return 0;
	        }
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
	        if (here.getDescription() != null) {
	            System.out.println("This is " + Grammar.indefiniteDescription(here));
	        }
	    } else {
	        Direction[] directions;
	        
	        if (args[0].equals("all")) {
	            directions = new Direction[] {
	                    Direction.n, Direction.ne, Direction.e, Direction.se, 
	                    Direction.s, Direction.sw, Direction.w, Direction.nw, 
	                    Direction.u, Direction.d, Direction.a, Direction.b}; 
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
	            seen |= lookInDirection(here, direction, actor.hasWorkingLight());
	        }
	        if (!seen) {
	            if (!actor.canSee()) {
	                System.out.println("It is too dark to see anything here.");
	            } else if (args[0].equals("all")) {
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
		return 1;
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

    private boolean lookInDirection(Location here, Direction direction, 
            boolean hasLight) {
        List<Vista> vistas = here.getVistas().stream()
                .filter((w) -> w.mainDirection().equals(direction))
                .toList();
        boolean seen = false;
        Wall wall = null;
        for (Vista vista: vistas) {
            String description = null;
            if (vista instanceof Exit) {
                Exit exit = ((Exit) vista);
                if (!here.isDark() || hasLight || !exit.getTo().isDark()) {
                    description = describeExit(exit, wall);
                }
            } else if (vista instanceof Wall) {
                wall = (Wall) vista;
                if (!here.isDark() || hasLight) {
                    description = describeWall(wall);
                }
            } else {
                if (!here.isDark() || hasLight) {
                    description = describeVista(vista);
                }
            }
            if (description != null && !description.isEmpty()) {
                if (!seen) {
                    System.out.print(describeDirection(direction) + " you see ");
                    seen = true;
                } else {
                    System.out.print("There is ");
                }
                System.out.println(description);
            }
        }
        return seen;
    }
	
    private String describeDirection(Direction direction) {
        return switch(direction) {
        case a -> "Above you";
        case b -> "Below you";
        case u -> "Upwards";
        case d -> "Downwards";
        default -> "To the " + direction.getName();
        };
    }

    private String describeVista(Vista vista) {
        String desc = vista.getDescription();
        if (desc == null) {
            desc = "an endless vista";
        }
        return desc;
    }

    private String describeWall(Wall wall) {
        String desc = wall.getDescription();
        if (desc == null) {
            desc = wall.getName();
        } if (desc == null) {
            desc = "a wall";
        } 
        List<Item> items = wall.getItemsOnWall();
        if (!items.isEmpty()) {
            desc += ". On the wall are the following:";
            for (Entity item: items) {
                desc += "\n   " + item.getAbbrev() + ": " + item.getName();
            }
        } 
        return desc;
    }

    private String describeExit(Exit exit, Wall wall) {
        String desc;
        Item portal = exit.getPortal();
        if (wall != null) {
            if (portal != null) {
                desc = "a " + portal.getAbbrev();
            } else {
                desc = "an exit";
            }
            if (portal == null || !portal.isOpenable() || portal.isOpen()) {
                if (exit.getTo().isDark()) {
                    desc += " to a dark area";
                } else {
                    desc += " to " + exit.getTo().getName();
                }
            }
            if (!wall.isFarAway()) {
                desc += " in the wall";  // TODO - add direction
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
                    desc = Grammar.indefiniteForm(desc);
                } else {
                    if (exit.getTo().isDark()) {
                        desc = "a dark area";
                    } else {
                        desc = Grammar.definiteForm(exit.getTo().getName());
                    }
                }
                // TODO - add direction
            } else {
                desc = Grammar.definiteForm(exit.getTo().getName());
            }
        }
        if (portal != null) {
            if (portal.isOpenable()) {
                desc += ". The " + portal.getAbbrev() 
                + (portal.isOpen() ? " is open" : " is closed");
            } else if (portal.isBlocked()) {
                desc += ". The " + portal.getAbbrev() 
                + ". The " + portal.getAbbrev() + " is blocked";
            } 
        }
        desc += ".";
        return desc;
    }

    @Override
    public String describe() {
        return "Describe the current location.";
    }
}
