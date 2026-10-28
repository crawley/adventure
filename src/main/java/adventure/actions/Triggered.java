package adventure.actions;

public class Triggered {
    
    public enum Verb {
        HIDE, REVEAL, OPEN, CLOSE, LOCK, UNLOCK, 
        MESSAGE, SAY, DECR_AND_TEST, RESET_IF_ZERO
    }
    
    private final String message;
    private final String target;
    private final String arg;
    private final Verb verb;
    private final boolean recurring;
    private boolean triggered = false;
    
    public Triggered(String target, String arg, Verb verb, 
            String message, boolean recurring) {
        this.target = target;
        this.arg = arg;
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
