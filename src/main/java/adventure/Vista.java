package adventure;

public class Vista {
    private final Direction[] directions;
    private final String description;

    public Vista(Direction[] directions, String description) {
        this.directions = directions;
        this.description = description;
    }

    public Vista(Direction direction, String description) {
        this(new Direction[] {direction}, description);
    }

    public Direction[] getDirections() {
        return directions;
    }

    public String getDescription() {
        return description;
    }

    public boolean matchesDirection(Direction direction) {
        for (Direction d: directions) {
            if (d == direction) {
                return true;
            }
        }
        return false;
    }
    
    public Direction mainDirection() {
        return directions[0];
    }
}
