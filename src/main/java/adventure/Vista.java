package adventure;

public class Vista {
    private final Direction direction;
    private final String description;

    public Vista(Direction direction, String description) {
        this.direction = direction;
        this.description = description;
    }

    public Direction getDirection() {
        return direction;
    }

    public String getDescription() {
        return description;
    }
}
