package adventure;

import java.util.List;

import adventure.actions.Trigger;

public class Pet extends Actor {
    
	public Pet(Kind kind, String label, String name, String abbrev,
	        String description, Location location, 
	        List<Trigger> triggers, PropertySet properties) {
		super(kind, label, name, abbrev, description, location, triggers, properties);
	}
    
    protected boolean eat(Item food) {
        if (!(Arena.getInstance().getAssociations().isRelated("LIKES", this, food))) {
            return false;
        }
        return super.eat(food);
    }

}
