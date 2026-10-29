package adventure;

import java.util.Map;
import java.util.TreeMap;

public class PropertySet {
    
    private final Map<String, Property> props;

    public PropertySet() {
        this.props = new TreeMap<>();
    }
    
    public void add(Property property) {
        props.put(property.getName(), property);
    }
    
    public Property lookup(String name) {
        return props.get(name);
    }

}
