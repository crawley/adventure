package adventure;

public class Wall {
    private final Direction direction;
    private final String name;
    private final String description;
    
    public Wall(Direction direction, String name, String description) {
        super();
        this.direction = direction;
        this.name = name;
        this.description = description;
    }

    public Direction getDirection() {
        return direction;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
