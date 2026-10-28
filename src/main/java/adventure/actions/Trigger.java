package adventure.actions;

import java.util.List;

public class Trigger {
    
    public enum Event {
        MOVED, SEARCHED, OPENED, CLOSED, DROPPED, 
        WORN, UNWORN, TAKEN, LOCKED, UNLOCKED, 
        ADDED, REMOVED, CONSUMED, TICK
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
