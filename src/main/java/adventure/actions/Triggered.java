package adventure.actions;

public class Triggered {
    
    public enum Verb {
        HIDE, REVEAL, OPEN, CLOSE, LOCK, UNLOCK, 
        MESSAGE, SAY, 
        INCREMENT, DECREMENT, TEST_MIN, TEST_MAX, SET_MIN, SET_MAX, 
        TEST, SET
    }
    
    private final String message;
    private final String target;
    private final String arg;
    private final String arg2;
    private final Verb verb;
    private final boolean recurring;
    private boolean triggered = false;
    
    public Triggered(String target, String arg, String arg2, Verb verb, 
            String message, boolean recurring) {
        this.target = target;
        this.arg = arg;
        this.arg2 = arg2;
        this.verb = verb;
        this.message = message;
        this.recurring = recurring;
    }

    public String getMessage() {
        return message;
    }

    public String getTarget() {
        return target;
    }

    public String getArg() {
        return arg;
    }
    
    public String getArg2() {
        return arg2;
    }

    public Verb getVerb() {
        return verb;
    }

    public boolean trigger() {
        if (recurring) {
            return true;
        } else if (triggered) {
            return false;
        } else {
            triggered = true;
            return true;
        }
    }
}
