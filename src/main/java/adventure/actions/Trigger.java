package adventure.actions;

import java.util.List;

public class Trigger {
    
    public enum Event {
        TICK, 
        MOVED, SEARCHED, OPENED, CLOSED, DROPPED, 
        WORN, UNWORN, TAKEN, LOCKED, UNLOCKED, 
        ADDED, REMOVED, CONSUMED, 
        PETTED
    }
    
    private final List<Event> events;
    private final List<Triggered> actions;
    
    public Trigger(List<Event> events, List<Triggered> actions) {
        this.actions = actions;
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }

    public List<Triggered> getActions() {
        return actions;
    }
}
