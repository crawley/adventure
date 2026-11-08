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
    private final Template base;
    private final List<Trigger> triggers;
    private final PropertySet properties;

    private static Map<String, Template> allTemplates = new HashMap<>();
    
    public Template(Kind kind, String label, Template base, List<Trigger> triggers, PropertySet properties) {
        super();
        this.kind = kind;
        this.label = label;
        this.base = base;
        if (base != null && base.kind != kind) {
            throw new ArenaException("Template kind inconsistent with its base template");
        }
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

    public Template getBase() {
        return base;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public PropertySet getProperties() {
        return properties;
    }
    
    public static Template lookup(String label) {
        Template template = allTemplates.get(label);
        if (template == null) {
            throw new ArenaException("No template with label '" + label + "'");
        } else {
            return template;
        }
    }
}
