package adventure;

public class Exit extends Vista {
    
    public enum ExitType {
        DOORWAY, ENTRANCE, PASSAGEWAY, STAIRWAY, LADDER,
        STEP, PATH, GATEWAY, TRACK, ROAD;
    }

    private final Item portal;
    private final Location to;
    private final ExitType type;

    public Exit(ExitType type, Direction[] directions, Item portal, Location to,
            String description) {
        super(directions, description, false);
        this.portal = portal;
        this.to = to;
        this.type = type;
    }

    public Item getPortal() {
        return portal;
    }

    public Location getTo() {
        return to;
    }

    public ExitType getType() {
        return type;
    }
}
