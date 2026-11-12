package adventure;

public class Exit extends Vista {
    private final Item portal;
    private final Location to;

    public Exit(Direction[] directions, Item portal, Location to,
            String description) {
        super(directions, description, false);
        this.portal = portal;
        this.to = to;
    }

    public Item getPortal() {
        return portal;
    }

    public Location getTo() {
        return to;
    }
}
