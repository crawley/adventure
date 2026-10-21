package adventure.actions;

import java.util.List;

import adventure.Item;
import adventure.Location;

public class VerbResult {
    
    public enum Outcome {
        SUCCESS, NONE, NOT_FOUND, NOT_UNIQUE, NOT_APPLICABLE, 
        ALREADY_DONE, NOT_POSSIBLE, FULL
    }

    private final Outcome outcome;
    private final String word;
    private final Item item;
    private final Item item2;
    private final Location location;
    private final String details;
    private final List<Item> alternatives;
    
    private VerbResult(Outcome outcome, Item item, Item item2, 
            Location location, String word, 
            String details, List<Item> alternatives) {
        super();
        this.outcome = outcome;
        this.item = item;
        this.item2 = item2;
        this.location = location;
        this.word = word;
        this.details = details;
        this.alternatives = alternatives;
    }
    
    public VerbResult(Outcome outcome, Item item) {
        this(outcome, item, null, null, null, null, null);
    }
    
    public VerbResult(Outcome outcome, Item item, Item item2) {
        this(outcome, item, item2, null, null, null, null);
    }
    
    public VerbResult(Outcome outcome, Location location) {
        this(outcome, null, null, location, null, null, null);
    }
    
    public VerbResult(Outcome outcome, String word) {
        this(outcome, null, null, null, word, null, null);
    }
    
    public VerbResult(Outcome outcome, String word, List<Item> alternatives) {
        this(outcome, null, null, null, word, null, alternatives);
    }
    
    public VerbResult(Outcome outcome) {
        this(outcome, null, null, null, null, null, null);
    }

    public VerbResult(Outcome outcome, Item item, String details) {
        this(outcome, item, null, null, null, details, null);
    }

    public Outcome getOutcome() {
        return outcome;
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

    public List<Item> getAlternatives() {
        return alternatives;
    }

    public Item getItem2() {
        return item2;
    }
}
