package adventure.actions;

import java.util.List;

public class Trigger {
    
    public enum Event {
        MOVED, SEARCHED, OPENED, CLOSED, DROPPED, 
        WORN, UNWORN, TAKEN, LOCKED, UNLOCKED, 
        ADDED, REMOVED
    }
    
    public enum Verb {
        HIDE, REVEAL, OPEN, CLOSE, LOCK, UNLOCK
    }
    
    private final List<Event> events;
    private final String message;
    private final String target;
    private final Verb verb;
    private boolean triggered = false;
    
    public Trigger(List<Event> events, String target, Verb verb, String message) {
        this.events = events;
        this.target = target;
        this.verb = verb;
        this.message = message;
    }
    
    public Trigger(List<Event> events, String message) {
        this(events, null, null, message);
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

    public Verb getVerb() {
        return verb;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }
}
