package adventure;

import java.util.List;

public class Container extends Item {
    
    private final EntityList<Item> contents;

    public Container(String label, String name, String abbrev, 
            String description, long flags, Template base,
            List<Trigger> triggers, PropertySet properties) {
        super(label, name, abbrev, description, flags, 
                base, triggers, properties);
        contents = new EntityList<>(label + "contents");
    }

    public List<Item> getContents() {
        return contents;
    }

    public void addItem(Item item) {
        contents.add(item);
    }

    public void removeItem(Item item) {
        contents.remove(item);
    }
}
