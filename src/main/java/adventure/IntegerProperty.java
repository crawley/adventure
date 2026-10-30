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

    public synchronized void setValue(int value) {
        if (value < min) {
            value = min;
        } else if (value > max) {
            value = max;
        }
        this.value = value;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public synchronized void increment() {
        setValue(value + 1);
    }

    @Override
    public synchronized void decrement() {
        setValue(value - 1);
    }
    
    @Override
    public boolean testMin() {
        return value == min;
    }

    @Override
    public boolean testMax() {
        return value == max;
    }
}
