package adventure;

import java.util.HashMap;
import java.util.Map;

import adventure.actions.Triggered;

public class Entity {

    private final String label;
    private final String name;
    private final String abbrev;
    
    // Can be changed by a trigger ...
    protected String description;
    protected final PropertySet properties;
    
    protected static Map<String, Entity> allEntities = new HashMap<>();

    /**
     * Construct an Entity
     * 
     * @param label a unique label for the entity
     * @param name a name for the entity
     * @param abbrev a short tag for use in commands.  If null, default to the label.
     * @param description a description for the entity.
     * @param properties the entity's initial property set
     * @throws EntityException if the label is non-unique.
     */
    public Entity(String label, String name, String abbrev, String description, 
            PropertySet properties) {
        super();
        this.label = label;
        this.name = name;
        this.abbrev = abbrev == null ? label : abbrev;
        this.description = description;
        this.properties = (properties == null) ? new PropertySet() : properties;

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
    
    public PropertySet getProperties() {
        return properties;
    }

    protected Number getNumericArg2(Triggered action) {
        String arg = action.getArg2();
        if (arg == null) {
            return null;
        } 
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException ex) {
            try {
                return Double.parseDouble(arg);
            } catch (NumberFormatException ex2) {
                throw new TriggerException(
                        "Action arg value ('" + arg + "') not a number");
            }
        }
    }

    protected NumberProperty getNumberProperty(String name) {
        Property prop = properties.lookup(name);
        if (prop == null) {
            throw new TriggerException(
                    "Can't find '" + name + "' property on '" +
                            getLabel() + "'");
        }
        if (prop instanceof NumberProperty) {
            return (NumberProperty) prop;
        } else {
            throw new TriggerException(
                    "Property '" + name + "' on '" + getLabel() +
                    "' not numeric");
        }
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