package adventure;

import java.util.HashMap;
import java.util.Map;

public class Entity {

    private final String label;
    private final String name;
    private final String abbrev;
    private final String description;
    
    protected static Map<String, Entity> allEntities = new HashMap<>();

    public Entity(String label, String name, String abbrev, String description) {
        super();
        this.label = label;
        this.name = name;
        this.abbrev = abbrev;
        this.description = description;

        if (allEntities.containsKey(label)) {
            throw new EntityException("Entity with label '" + label + "' already exists");
        }
        allEntities.put(label,  this);
    }

    /**
     * @return The unique label for the Entity
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return A name for the Entity (including 'a', 'the', 'my', etc)
     */
    public String getName() {
        return name;
    }

    /**
     * @return A short label for the Entity.  May be non-unique.
     */
    public String getAbbrev() {
        return abbrev;
    }

    /**
     * @return A text description for the Entity.
     */
    public String getDescription() {
        return description;
    }
    
    protected static Entity lookup(String label) {
        return allEntities.get(label);
    }

}