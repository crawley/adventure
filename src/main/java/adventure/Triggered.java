package adventure;

public class Triggered {
    
    public enum Verb {
        HIDE, REVEAL, OPEN, CLOSE, LOCK, UNLOCK, DROP, DESTROY,
        MESSAGE, SAY, THINK, 
        INCREMENT, DECREMENT, TEST_MIN, TEST_MAX, SET_MIN, SET_MAX, 
        TEST, TEST_RELATED, TEST_UNRELATED,
        SET_PROPERTY, SET_DESCRIPTION, SET_FLAG, CLEAR_FLAG, SET_NAME, SET_ABBREV, 
        SET_OTHER_DESCRIPTION, SET_OTHER_NAME, SET_OTHER_ABBREV, 
        SET_OTHER_PROPERTY, SET_OTHER_FLAG, CLEAR_OTHER_FLAG,
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
