package adventure;

import java.util.List;

public class Room extends Location {

    private final List<Wall> walls;

    public Room(String label, String name, String abbrev, String description, List<Wall> walls) {
        super(label, name, abbrev, description);
        
        this.walls = walls;
    }

    public List<Wall> getWalls() {
        return walls;
    }
}
