package adventure.actions;

import java.util.List;
import java.util.Set;

import adventure.Actor;
import adventure.BooleanProperty;
import adventure.Entity;
import adventure.FloatProperty;
import adventure.IntegerProperty;
import adventure.Person;
import adventure.Property;
import adventure.PropertySet;

public class StatisticsAction implements Action {

	@Override
	public int perform(Person actor, String[] args) {
	    Entity target;
	    if (args.length == 0) {
	        target = actor;
	    } else if (args.length == 1) {
	        List<Actor> candidates = actor.getLocation().actors()
	                .stream()
	                .filter((a) -> a.getAbbrev().equals(args[0])
	                        || a.getName().equals(args[0]))
	                .toList();
	        if (candidates.isEmpty()) {
	            System.out.println("No '" + args[0] + "' here");
	            return 0;
	        } else if (candidates.size() > 1) {
                System.out.println(
                        "Actor " + args[0] + "' is ambiguous");
                return 0;
            } else {
                target = candidates.get(0);
            }
	    } else {
	        System.out.println("Only one actor please");
	        return 0;
	    }
	    
	    PropertySet props = target.getProperties();
	    Set<String> keys = props.keys();
	    if (keys.isEmpty()) {
	        if (target == actor) {
	            System.out.println("You have no statistics.");
	        } else {
	            System.out.println("The " + target.getAbbrev() 
	            + " has no statistics.");
	        }
	    } else {
	        if (target == actor) {
	            System.out.println("Your statistics:");
	        } else {
	            System.out.println("The " + target.getAbbrev() 
	            + "'s statistics:");
	        }

	        for (String name: keys) {
	            Property prop = props.lookup(name);
	            System.out.print("  " + name + " = ");
	            if (prop instanceof BooleanProperty) {
	                System.out.println( 
	                        (prop.isTrue() ? "TRUE" : "FALSE"));
	            } else if (prop instanceof IntegerProperty) {
	                System.out.println(prop.intValue());
	            } else if (prop instanceof FloatProperty) {
	                System.out.println(prop.floatValue());
	            } else {
	                System.out.println("???");
	            }
	        }
	    }
	    return 0;
	}

    @Override
    public String describe() {
        return "Show your or another actor's statistics.";
    }
}
