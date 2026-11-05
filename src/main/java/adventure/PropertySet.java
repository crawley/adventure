package adventure;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PropertySet {
    
    private final Map<String, Property> props;

    public PropertySet() {
        this.props = new TreeMap<>();
    }
    
    public void add(Property property) {
        Property current = props.get(property.getName());
        if (current != null) {
            if (property.getClass() != current.getClass()) {
                throw new EntityException(
                        "Inconsistent type for added property '" 
                                + property.getName() + "'");
            }
        }
        props.put(property.getName(), property);
    }
    
    public Property lookup(String name) {
        return props.get(name);
    }

    public Set<String> keys() {
        return props.keySet();
    }
}
