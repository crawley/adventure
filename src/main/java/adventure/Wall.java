package adventure;

import java.util.List;

public class Wall extends Vista {
    private final String name;
    private final EntityList<Item> itemsOnWall;
    
    public Wall(Direction direction, String name, String description, boolean farAway) {
        super(new Direction[] {direction}, description, farAway);
        this.name = name;
        this.itemsOnWall = new EntityList<>("on-wall-" + name + "-" + direction);
    }

    public String getName() {
        return name;
    }

    public List<Item> getItemsOnWall() {
        return itemsOnWall;
    }
}
