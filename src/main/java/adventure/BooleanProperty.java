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
    public String stringValue() {
        return value ? "Y" : "N";
    }

    @Override
    public boolean isTrue() {
        return value;
    }

    public synchronized boolean getValue() {
        return value;
    }

    public synchronized void setValue(boolean newValue) {
        this.value = newValue;
    }

    public void set(Boolean newValue) {
        setValue(newValue.booleanValue());
    }

    @Override
    public Property copy() {
        return new BooleanProperty(getName(), value);
    }
}
