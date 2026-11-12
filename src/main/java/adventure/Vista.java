package adventure;

public class Vista {
    private final Direction[] directions;
    private final String description;
    private final boolean farAway;

    public Vista(Direction[] directions, String description, boolean farAway) {
        this.directions = directions;
        this.description = description;
        this.farAway = farAway;
    }

    public Vista(Direction direction, String description, boolean farAway) {
        this(new Direction[] {direction}, description, farAway);
    }

    public Direction[] getDirections() {
        return directions;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFarAway() {
        return farAway;
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
