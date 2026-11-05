package adventure;

import java.util.List;

import adventure.actions.Grammar;
import adventure.actions.Trigger;
import adventure.actions.Triggered;

public abstract class Actor extends Entity {
    
    public enum Kind {
        PLAYER, NPC, PET, MONSTER
    }
    
    private final Kind kind;    
    private Location location;
    
	public Actor(Kind kind, String label, String name, String abbrev,
	        String description, Location location, 
	        List<Trigger> triggers, PropertySet properties) {
		super(label, name, abbrev, description, triggers, properties);
		this.location = location;
		this.kind = kind;
	}

	public Kind getKind() {
        return kind;
    }

    public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
    
	protected boolean eat(Item food) {
        NumberProperty calories = food.getNumberProperty("calories");
        if (calories == null) {
            return true;
        }
        NumberProperty hunger = this.getNumberProperty("hunger");
        if (hunger == null) {
            return true;
        }
        hunger.decrement(calories.floatValue());
        if (hunger.floatValue() == 0) {
            if (this == Arena.getInstance().getPlayer()) {
                System.out.println("You are satiated");
            } else {
                String name = Grammar.definiteName(this);
                System.out.println(
                        Grammar.capitalize(name) + " is satiated");
            }
        }
        return true;
    }
    
    @Override
    protected boolean doTrigger(Triggered action, Entity target) {
        switch (action.getVerb()) {
        case THINK:
            if (this.getLabel().equals("self") && action.trigger()) {
                System.out.println("You think: " + action.getMessage());
            }
            break;
        case SAY:
            for (Entity actor: location.actors()) {
                if (actor != this && action.trigger()) {
                    String name = Grammar.definiteName(this);
                    System.out.println(
                            Grammar.capitalize(name)
                            + " says: " + action.getMessage());
                }
            }
            break;
        default:
            return super.doTrigger(action, target);
        }
        return true;
    }
}
