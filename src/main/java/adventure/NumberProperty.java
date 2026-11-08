package adventure;

public abstract class NumberProperty  extends Property {
    
    public NumberProperty(String name) {
        super(name);
    }
    
    public abstract void increment(Number by);

    public abstract void decrement(Number by);
    
    public abstract void set(Number value);
    
    public abstract boolean testMin();

    public abstract boolean testMax();
}
