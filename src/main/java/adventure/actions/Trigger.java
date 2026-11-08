package adventure.actions;

import java.util.List;

public class Trigger {
    
    public enum Event {
        TICK, 
        MOVED, SEARCHED, OPENED, CLOSED, DROPPED, 
        WORN, UNWORN, TAKEN, LOCKED, UNLOCKED, 
        ADDED, REMOVED, CONSUMED, 
        PETTED, ATE, REFUSED
    }
    
    private final List<Event> events;
    private final List<Triggered> actions;
    private final boolean override;
    
    public Trigger(List<Event> events, List<Triggered> actions, boolean override) {
        this.actions = actions;
        this.events = events;
        this.override = override;
    }

    public List<Event> getEvents() {
        return events;
    }

    public List<Triggered> getActions() {
        return actions;
    }

    public boolean isOverride() {
        return override;
    }
}
