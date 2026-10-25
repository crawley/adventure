package adventure;


public enum Direction {
    n("north"),
    ne("northeast"),
    e("east"),
    se("southeast"),
    s("south"),
    sw("southwest"),
    w("west"),
    nw("northwest"),
    u("up"),
    d("down"),
    i("in"),
    o("out"),
    a("above"),
    b("below");
    
    private final String name;
    
    Direction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
