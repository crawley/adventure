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
	        String description, long flags, Location location, Template base,
	        List<Trigger> triggers, PropertySet properties) {
		super(label, name, abbrev, description, flags, base, triggers, properties);
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
	
	public boolean hasWorkingLight() {
	    StringProperty prop = (StringProperty) properties.lookup("light");
	    if (prop == null)
	        return false;
	    String name = prop.getValue().trim();
	    if (name.isEmpty()) {
	        return false;
	    }
	    Item item = Item.lookupItem(name);
	    if (item.isaLight()) {
	        return false;
	    }
	    if (item.isSwitchable() && !item.isOn()) {
	        return false;
	    }
	    return true;
	}
	
	public boolean canSee() {
	    return !location.isDark() || hasWorkingLight();
	}
    
	protected boolean eat(Item food) {
        NumberProperty calories = food.getNumberProperty("calories");
        NumberProperty hunger = this.getNumberProperty("hunger");
        if (calories == null || hunger == null) {
            return true;
        }
        if (hunger.floatValue() == 0) {
            if (this == Arena.getInstance().getPlayer()) {
                System.out.println("You are not hungry");
            } else {
                System.out.println("It is not hungry");
            }
            return false;
        }
        if (calories.intValue() == 0) {
            if (this == Arena.getInstance().getPlayer()) {
                System.out.println("That was not satisfying");
            }
        } else {
            hunger.decrement(calories.floatValue()); 
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
    protected boolean doTrigger(Triggered action, Entity other) {
        Actor player = Arena.getInstance().getPlayer();
        switch (action.getVerb()) {
        case THINK:
            if (this == player && action.trigger()) {
                // Can't read the thoughts of another actor ...
                System.out.println("You think: " + action.getMessage());
            }
            break;
        case SAY:
            if (this == player && action.trigger()) {
                // Talking to yourself ...
                System.out.println("You say: " + action.getMessage());
            } else if (player.getLocation() == location && action.trigger()) {
                // Can only hear things said in the location you are in.
                // TODO ... what about hearing voices in *nearby* locations?
                String name = Grammar.definiteName(this);
                System.out.println(
                        Grammar.capitalize(name)
                        + " says: " + action.getMessage());
            }
            break;
        default:
            return super.doTrigger(action, other);
        }
        return true;
    }
}
