package adventure;

public class StringProperty extends Property 
implements Cloneable {
    
    private String value;

    public StringProperty(String name, String initial) {
        super(name);
        this.value = initial;
    }
    
    @Override
    public int intValue() {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    @Override
    public double floatValue() {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }
    
    @Override
    public String stringValue() {
        return getValue();
    }

    @Override
    public boolean isTrue() {
        return !(value.trim().isEmpty());
    }

    public synchronized String getValue() {
        return value;
    }

    public synchronized void setValue(String newValue) {
        this.value = newValue;
    }

    public void set(String newValue) {
        setValue(newValue);
    }

    @Override
    public Property copy() {
        return new StringProperty(getName(), value);
    }
}
