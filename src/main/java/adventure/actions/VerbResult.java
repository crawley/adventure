package adventure.actions;

import java.util.List;

import adventure.Actor;
import adventure.Entity;
import adventure.Item;
import adventure.Location;

public class VerbResult {
    
    public enum Outcome {
        SUCCESS, NONE, NOT_FOUND, NOT_UNIQUE, NOT_APPLICABLE, 
        ALREADY_DONE, NOT_POSSIBLE, FULL, NOT_ON_SELF
    }

    private final Outcome outcome;
    private final int timeTaken;
    private final String word;
    private final Actor actor;
    private final Item item;
    private final Item item2;
    private final Location location;
    private final String details;
    private final List<? extends Entity> alternatives;
    
    private VerbResult(Outcome outcome, int timeTaken, 
            Actor actor, Item item, Item item2, 
            Location location, String word, 
            String details, List<? extends Entity> alternatives) {
        super();
        this.outcome = outcome;
        this.timeTaken = timeTaken;
        this.actor = actor;
        this.item = item;
        this.item2 = item2;
        this.location = location;
        this.word = word;
        this.details = details;
        this.alternatives = alternatives;
    }
    
    public VerbResult(int timeTaken, Item item) {
        this(Outcome.SUCCESS, timeTaken, null, item, null, null, null, null, null);
    }
    
    public VerbResult(int timeTaken, Actor actor) {
        this(Outcome.SUCCESS, timeTaken, actor, null, null, null, null, null, null);
    }
    
    public VerbResult(int timeTaken, Item item, Item item2) {
        this(Outcome.SUCCESS, timeTaken, null, item, item2, null, null, null, null);
    }
   
    public VerbResult(Outcome outcome, Item item) {
        this(outcome, 0, null, item, null, null, null, null, null);
    }
    
    public VerbResult(Outcome outcome, Actor actor) {
        this(outcome, 0, actor, null, null, null, null, null, null);
    }

    public VerbResult(Outcome outcome, Item item, Item item2) {
        this(outcome, 0, null, item, item2, null, null, null, null);
    }
    
    public VerbResult(Outcome outcome, Location location) {
        this(outcome, 0, null, null, null, location, null, null, null);
    }
    
    public VerbResult(Outcome outcome, String word) {
        this(outcome, 0, null, null, null, null, word, null, null);
    }
    
    public VerbResult(Outcome outcome, String word, 
            List<? extends Entity> alternatives) {
        this(outcome, 0, null, null, null, null, word, null, alternatives);
    }
    
    public VerbResult(Outcome outcome) {
        this(outcome, 0, null, null, null, null, null, null, null);
    }

    public VerbResult(Outcome outcome, Item item, String details) {
        this(outcome, 0, null, item, null, null, null, details, null);
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public Actor getActor() {
        return actor;
    }

    public Item getItem() {
        return item;
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
