package adventure;

public class IntegerProperty extends Property {
    
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
    public float floatValue() {
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

}
