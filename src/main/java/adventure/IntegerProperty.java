package adventure;

public class IntegerProperty extends NumberProperty {
    
    private final int min;
    private final int max;
    private int value;

    public IntegerProperty(String name, int initial, int min, int max) {
        super(name);
        if (min > max) {
            throw new IllegalArgumentException("min > max");
        }
        this.min = min;
        this.max = max;
        this.setValue(initial);
    }
    
    @Override
    public int intValue() {
        return getValue();
    }

    @Override
    public double floatValue() {
        return getValue();
    }

    public synchronized int getValue() {
        return value;
    }

    public synchronized void setValue(int newvalue) {
        if (newvalue < min) {
            this.value = min;
        } else if (newvalue > max) {
            this.value = max;
        } else {
            this.value = newvalue;
        }
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public synchronized void increment(Number by) {
        if (by == null) {
            setValue(value + 1);
        } else {
            setValue(value + by.intValue());
        }
    }

    @Override
    public synchronized void decrement(Number by) {
        if (by == null) {
            setValue(value - 1);
        } else {
            setValue(value - by.intValue());
        }
    }
    
    public void set(Number newValue) {
        setValue(newValue.intValue());
    }
    
    @Override
    public boolean testMin() {
        return value == min;
    }

    @Override
    public boolean testMax() {
        return value == max;
    }
    
    @Override
    public Property copy() {
        return new FloatProperty(getName(), value, min, max);
    }
}
