package adventure;

public class Exit {
    private final Direction direction;
	private final Item portal;
	private final Location to;

	public Exit(Direction direction, Item portal, Location to) {
    	this.direction = direction;
        this.portal = portal;
    	this.to = to;
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
}
