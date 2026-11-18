package adventure;

import java.util.List;

public class Pet extends Actor {
    
	public Pet(Kind kind, String label, String name, String abbrev,
	        String description, long flags, Location location, Template base,
	        List<Trigger> triggers, PropertySet properties) {
		super(kind, label, name, abbrev, description, flags, location, 
		        base, triggers, properties);
	}
    
    protected boolean eat(Item food) {
        if (!(Arena.getInstance().getAssociations().isRelated("LIKES", this, food))) {
            return false;
        }
        return super.eat(food);
    }

}
