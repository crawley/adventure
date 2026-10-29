package adventure;

public class FloatProperty extends Property {
    
    private final float min;
    private final float max;
    private float value;

    public FloatProperty(String name, float initial, float min, float max) {
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
    public float floatValue() {
        return getValue();
    }

    @Override
    public boolean isTrue() {
        return intValue() != 0;
    }

    public synchronized float getValue() {
        return value;
    }

    public synchronized void setValue(float value) {
        if (value < min) {
            value = min;
        } else if (value > max) {
            value = max;
        }
        this.value = value;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

}
