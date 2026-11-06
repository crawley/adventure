package adventure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adventure.actions.Trigger;

public class Template {
    
    public enum Kind {
        ACTOR, ITEM, LOCATION
    }
    
    private final Kind kind;
    private final String label;
    private final List<Template> supers;
    private final List<Trigger> triggers;
    private final PropertySet properties;

    private static Map<String, Template> allTemplates = new HashMap<>();
    
    public Template(Kind kind, String label, List<Template> supers, List<Trigger> triggers, PropertySet properties) {
        super();
        this.kind = kind;
        this.label = label;
        this.supers = supers;
        this.triggers = triggers;
        this.properties = properties;
        if (allTemplates.get(label) != null) {
            throw new ArenaException("Duplicate template name");
        }
        allTemplates.put(label, this);
    }

    public Kind getKind() {
        return kind;
    }

    public String getLabel() {
        return label;
    }

    public List<Template> getSupers() {
        return supers;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public PropertySet getProperties() {
        return properties;
    }
    
    public static Template lookup(String label) {
        return allTemplates.get(label);
    }
}
