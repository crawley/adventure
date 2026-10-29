package adventure;

public class BooleanProperty extends Property {
    
    private boolean value;

    public BooleanProperty(String name, boolean initial) {
        super(name);
        this.value = initial;
    }
    
    @Override
    public int intValue() {
        return value ? 1 : 0;
    }

    @Override
    public float floatValue() {
        return value ? 1.0f : 0.0f;
    }

    @Override
    public boolean isTrue() {
        return value;
    }

    public synchronized boolean getValue() {
        return value;
    }

    public synchronized void setValue(boolean value) {
        this.value = value;
    }
}
