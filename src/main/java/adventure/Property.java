package adventure;

public abstract class Property {
    private final String name;
    
    public Property(String name) {
        this.name = name;
    }
    
    public boolean isTrue() {
        return intValue() != 0;
    }
    
    public abstract int intValue();
    
    public abstract float floatValue();
    
    public String getName() {
        return name;
    }
}
