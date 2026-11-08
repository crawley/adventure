package adventure;

public class BooleanProperty extends Property 
implements Cloneable {
    
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
    public double floatValue() {
        return value ? 1.0 : 0.0;
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

    @Override
    public Property copy() {
        return new BooleanProperty(getName(), value);
    }
}
