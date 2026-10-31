package adventure;

import java.util.HashMap;
import java.util.Map;

public class Entity {

    private final String label;
    private final String name;
    private final String abbrev;
    private final String description;
    
    protected static Map<String, Entity> allEntities = new HashMap<>();

    /**
     * Construct an Entity
     * 
     * @param label a unique label for the entity
     * @param name a name for the entity
     * @param abbrev a short tag for use in commands.  If null, default to the label.
     * @param description a description for the entity.
     * @throws EntityException if the label is non-unique.
     */
    public Entity(String label, String name, String abbrev, String description) {
        super();
        this.label = label;
        this.name = name;
        this.abbrev = abbrev == null ? label : abbrev;
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
    
    /**
     * Lookup an entity via its label.
     * @param label
     * @return the entity or null if there is no match.
     */
    protected static Entity lookup(String label) {
        return allEntities.get(label);
    }

}