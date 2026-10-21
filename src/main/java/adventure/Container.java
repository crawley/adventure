package adventure;

import java.util.List;

import adventure.actions.Trigger;

public class Container extends Item {
    
    private final List<Item> contents;

    public Container(String label, String name, String abbrev, 
            String description, long flags, List<Trigger> triggers) {
        super(label, name, abbrev, description, flags, triggers);
        contents = new ItemList(label + "contents");
    }

    public List<Item> getContents() {
        return contents;
    }

    public void addItem(Item item) {
        contents.add(item);
        item.setContainer(this);
    }

    public void removeItem(Item item) {
        contents.remove(item);
        item.setContainer(null);
    }
}
