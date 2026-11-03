package adventure.actions;

import java.util.List;

import adventure.Entity;
import adventure.Item;
import adventure.Location;

public class VerbResult {
    
    public enum Outcome {
        SUCCESS, NONE, NOT_FOUND, NOT_UNIQUE, NOT_APPLICABLE, 
        ALREADY_DONE, NOT_POSSIBLE, NOT_ON_SELF
    }

    private final Outcome outcome;
    private final int timeTaken;
    private final String word;
    private final Entity target;
    private final Item item2;
    private final Location location;
    private final String details;
    private final List<? extends Entity> alternatives;
    
    private VerbResult(Outcome outcome, int timeTaken, 
            Entity target, Item item2, 
            Location location, String word, 
            String details, List<? extends Entity> alternatives) {
        super();
        this.outcome = outcome;
        this.timeTaken = timeTaken;
        this.target = target;
        this.item2 = item2;
        this.location = location;
        this.word = word;
        this.details = details;
        this.alternatives = alternatives;
    }
    
    public VerbResult(int timeTaken, Entity target) {
        this(Outcome.SUCCESS, timeTaken, target, null, null, null, null, null);
    }
        
    public VerbResult(int timeTaken, Entity target, Item item) {
        this(Outcome.SUCCESS, timeTaken, target, item, null, null, null, null);
    }
       
    public VerbResult(Outcome outcome, Entity target) {
        this(outcome, 0, target, null, null, null, null, null);
    }

    public VerbResult(Outcome outcome, Entity target, Item item2) {
        this(outcome, 0, target, item2, null, null, null, null);
    }
    
    public VerbResult(Outcome outcome, Location location) {
        this(outcome, 0, null, null, location, null, null, null);
    }
    
    public VerbResult(Outcome outcome, String word) {
        this(outcome, 0, null, null, null, word, null, null);
    }
    
    public VerbResult(Outcome outcome, String word, 
            List<? extends Entity> alternatives) {
        this(outcome, 0, null, null, null, word, null, alternatives);
    }
    
    public VerbResult(Outcome outcome) {
        this(outcome, 0, null, null, null, null, null, null);
    }

    public VerbResult(Outcome outcome, Entity target, String details) {
        this(outcome, 0, target, null, null, null, details, null);
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public Entity getTarget() {
        return target;
    }

    public Location getLocation() {
        return location;
    }

    public String getWord() {
        return word;
    }

    public String getDetails() {
        return details;
    }

    public List<? extends Entity> getAlternatives() {
        return alternatives;
    }

    public Item getItem2() {
        return item2;
    }

    public int getTimeTaken() {
        return timeTaken;
    }
}
