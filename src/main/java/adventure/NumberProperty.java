package adventure;

public abstract class NumberProperty  extends Property {
    
    public NumberProperty(String name) {
        super(name);
    }
    
    public abstract void increment();

    public abstract void decrement();
    
    public abstract boolean testMin();

    public abstract boolean testMax();
}
