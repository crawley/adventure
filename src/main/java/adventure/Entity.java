package adventure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adventure.Triggered.Verb;

public abstract class Entity {
    
    public record FlagDescriptor (
            long flag, 
            String property,
            boolean initial) {
        public FlagDescriptor(long flag) {
            this(flag, null, false);
        }
    }
    
    private final String label;
    
    // These can be changed by a trigger action ...
    private String description;
    private String name;
    private String abbrev;
    private long flags;

    protected final Template base;
    protected final List<Trigger> triggers;
    protected final PropertySet properties;
    
    private boolean destroyed;
    private EntityList<?> memberOf; 
    
    protected static Map<String, Entity> allEntities = new HashMap<>();

    /**
     * Construct an Entity
     * 
     * @param label a unique label for the entity
     * @param name a name for the entity
     * @param abbrev a short tag for use in commands.  If null, default to the label.
     * @param description a description for the entity.
     * @param flags
     * @param triggers 
     * @param properties the entity's initial property set
     * @throws EntityException if the label is non-unique.
     */
    public Entity(String label, String name, String abbrev, String description, 
            long flags, Template base, List<Trigger> triggers, PropertySet properties) {
        super();
        this.label = label;
        this.name = name;
        this.abbrev = abbrev == null ? label : abbrev;
        this.description = description;
        this.flags = flags;
        this.base = base;
        this.triggers = (triggers == null) ? new ArrayList<>() : triggers;
        this.properties = (properties == null) ? new PropertySet() : properties;
        mergeBaseProperties(base);

        if (allEntities.containsKey(label)) {
            throw new EntityException("Entity with label '" + label + "' already exists");
        }
        allEntities.put(label,  this);
    }

    /**
     * Merge in the properties from the base template chain.
     * 
     * @param base the base template.
     */
    private void mergeBaseProperties(Template base) {
        while (base != null) {
            PropertySet baseProps = base.getProperties();
            for (String key: baseProps.keys()) {
                Property baseProp = baseProps.lookup(key);
                Property prop = properties.lookup(key);
                if (prop == null) {
                    properties.add(baseProp.copy());
                } else {
                    if (prop.getClass() != baseProp.getClass()) {
                        throw new EntityException(
                                "Type inconsistent with base property '"
                                        + key + "'");
                    }
                }
            }
            base = base.getBase();
        }
    }
    
    protected final void addFlagProperties(
            Map<String, FlagDescriptor> descriptors) {
        for (FlagDescriptor desc: descriptors.values()) {
            if (desc.property() != null && ((this.flags & desc.flag()) != 0)) {
                if (this.properties.lookup(desc.property()) == null) {
                    BooleanProperty prop = new BooleanProperty(
                            desc.property(), desc.initial());
                    this.properties.add(prop);
                }
            }
        }
    }


    public EntityList<?> getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(EntityList<?> memberOf) {
        this.memberOf = memberOf;
    }

    /**
     * @return The unique label for the Entity
     */
    public String getLabel() {
        return label;
    }
    
    public String toString() {
        return getLabel();
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
    
    protected long getFlags() {
        return flags;
    }

    protected void setFlags(long flags) {
        this.flags = flags;
    }
    
    protected synchronized void setFlag(long flag, boolean yes) {
        long flags = getFlags();
        if (yes) {
            flags |= flag;
        } else {
            flags &= ~flag;
        }
        setFlags(flags);
    }

    public PropertySet getProperties() {
        return properties;
    }
    
    public boolean isHidden() {
        return false;
    }
    
    public boolean isDestroyed() {
        return destroyed;
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
    
    protected Boolean getBooleanArg2(Triggered action) {
        String arg = action.getArg2();
        if (arg == null) {
            return null;
        } 
        switch (arg.trim().toUpperCase()) {
        case "Y", "YES", "TRUE", "1": return true;
        case "N", "NO", "FALSE", "0": return false;
        default:
            throw new TriggerException(
                    "Action arg value ('" + arg + "') not a boolean");
        }
    }
        
    protected Property getProperty(String name) {
        Property prop = properties.lookup(name);
        if (prop == null) {
            throw new TriggerException(
                    "Can't find '" + name + "' property on '" +
                            getLabel() + "'");
        }
        return prop;
    }

    protected NumberProperty getNumberProperty(String name) {
        Property prop = getProperty(name);
        if (prop instanceof NumberProperty) {
            return (NumberProperty) prop;
        } else {
            throw new TriggerException(
                    "Property '" + name + "' on '" + getLabel() +
                    "' not numeric");
        }
    }

    public Template getBase() {
        return base;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public void trigger(Trigger.Event event) {
        trigger(event, null);
    }

    public void trigger(Trigger.Event event, Entity other) {
        boolean override = false;
        List<Trigger> triggers = this.triggers;
        Template base = this.base;
        while (triggers != null) {
            for (Trigger trigger: triggers) {
                if (!trigger.getEvents().contains(event)) {
                    continue;
                }
                for (Triggered action: trigger.getActions()) {
                    if (!doTrigger(action, other)) { 
                        break;
                    }
                }
                override |= trigger.isOverride();
            }
            if (override) {
                break;
            }
            if (base != null) {
                triggers = base.getTriggers();
                base = base.getBase();
            } else {
                triggers = null;
            }
        }
    }
    
    protected boolean doTrigger(Triggered action, Entity other) {
        Verb verb = action.getVerb();
        switch (verb) {
        case INCREMENT:
            Number number = getNumericArg2(action);
            getNumberProperty(action.getArg()).increment(number);
            break;
        case DECREMENT:
            number = getNumericArg2(action);
            getNumberProperty(action.getArg()).decrement(number);
            break;
        case SET_PROPERTY:
            Property prop = getProperty(action.getArg());
            doSetProperty(action, prop);
            break;
        case TEST_MIN:
            return getNumberProperty(action.getArg()).testMin();
        case TEST_MAX:
            return getNumberProperty(action.getArg()).testMax();
        case TEST_RELATED:
            return Arena.getInstance().getAssociations()
                    .isRelated(action.getArg(), this, other);
        case TEST_UNRELATED:
            return !(Arena.getInstance().getAssociations()
                    .isRelated(action.getArg(), this, other));
        case SET_DESCRIPTION:
            this.description = action.getArg();
            break;
        case SET_NAME:
            this.name = action.getArg();
            break;
        case SET_ABBREV:
            this.abbrev = action.getArg();
            break;
        case SET_OTHER_DESCRIPTION:
            if (other != null) {
                other.description = action.getArg();
            }
            break;
        case SET_OTHER_NAME:
            if (other != null) {
                other.name = action.getArg();
            }
            break;
        case SET_OTHER_ABBREV:
            if (other != null) {
                other.abbrev = action.getArg();
            }
            break;
        case SET_OTHER_PROPERTY:
            prop = other.properties.lookup(action.getArg());
            doSetProperty(action, prop);
            break;
        case MESSAGE:
            if (action.trigger() && action.getMessage() != null) {
                System.out.println(action.getMessage());
            }
            break;
        default:
            throw new TriggerException(
                    "Verb '" + verb + "' not implemented");
        }
        return true;
    }

    private void doSetProperty(Triggered action, Property prop) {
        if (prop instanceof NumberProperty) {
            ((NumberProperty) prop).set(getNumericArg2(action));
        } else if (prop instanceof StringProperty) {
            ((StringProperty) prop).set(action.getArg2());
        } else if (prop instanceof BooleanProperty) {
            ((BooleanProperty) prop).set(getBooleanArg2(action));
        } else {
            throw new UnsupportedOperationException("set property for " + prop.getClass());
        }
    }

    public void destroy() {
        destroyed = true;
        allEntities.remove(this.getLabel());
        if (memberOf != null) {
            memberOf.remove(this);
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
    
    public static Entity lookupEntity(String label) {
        if (label == null) {
            return null;
        }
        Entity entity = Entity.lookup(label);
        if (entity == null) {
            throw new EntityException("No entity with label '" + label + "'");
        } else {
            return entity;
        }
    }
}