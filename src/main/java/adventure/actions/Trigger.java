package adventure.actions;

import java.util.List;

public class Trigger {
    
    public enum Event {
        MOVED, SEARCHED, OPENED, CLOSED, DROPPED, 
        WORN, UNWORN, TAKEN, LOCKED, UNLOCKED, 
        ADDED, REMOVED, CONSUMED, TICK
    }
    
    public enum Verb {
        HIDE, REVEAL, OPEN, CLOSE, LOCK, UNLOCK, 
        SAY, DECR_AND_TEST, RESET_IF_ZERO
    }
    
    private final List<Event> events;
    private final String message;
    private final String target;
    private final String arg;
    private final Verb verb;
    private final boolean recurring;
    private boolean triggered = false;
    
    public Trigger(List<Event> events, String target, String arg, Verb verb, 
            String message, boolean recurring) {
        this.events = events;
        this.target = target;
        this.arg = arg;
        this.verb = verb;
        this.message = message;
        this.recurring = recurring;
    }
    
    public Trigger(List<Event> events, String message, boolean recurring) {
        this(events, null, null, null, message, recurring);
    }

    public List<Event> getEvents() {
        return events;
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
