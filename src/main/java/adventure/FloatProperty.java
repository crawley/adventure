package adventure;

public class FloatProperty extends NumberProperty {
    
    private final double min;
    private final double max;
    private double value;

    public FloatProperty(String name, double initial, double min, double max) {
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
        return (int) getValue();
    }

    @Override
    public double floatValue() {
        return getValue();
    }

    @Override
    public boolean isTrue() {
        return intValue() != 0;
    }

    public synchronized double getValue() {
        return value;
    }

    public synchronized void setValue(double value) {
        if (value < min) {
            value = min;
        } else if (value > max) {
            value = max;
        }
        this.value = value;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
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
