package adventure;

public class Exit {
    private final Direction direction;
    private final Item portal;
    private final Location to;
    private final String description;

    public Exit(Direction direction, Item portal, Location to,
            String description) {
        this.direction = direction;
        this.portal = portal;
        this.to = to;
        this.description = description;
    }

    public Direction getDirection() {
        return direction;
    }

    public Item getPortal() {
        return portal;
    }

    public Location getTo() {
        return to;
    }

    public String getDescription() {
        return description;
    }
}
