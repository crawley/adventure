package adventure.actions;

import java.util.List;

import adventure.Entity;
import adventure.Person;

public class ActionResult {
    
    public enum Outcome {
        SUCCESS, NONE, NOT_FOUND, NOT_UNIQUE, NOT_APPLICABLE, 
        ALREADY_DONE, NOT_POSSIBLE, NOT_ON_SELF, CANNOT_SEE, SYNTAX,
    }

    private final Outcome outcome;
    private final int timeTaken;
    private final String message;
    private final String word;
    private final List<? extends Entity> alternatives;
    
    ActionResult(Outcome outcome, int timeTaken, String message, 
            String word, List<? extends Entity> alternatives) {
        super();
        this.outcome = outcome;
        this.timeTaken = timeTaken;
        this.message = message;
        this.word = word;
        this.alternatives = alternatives;
    }
    
    public ActionResult(Outcome outcome, String message) {
        this(outcome, 0, message, null, null);
    }

    public ActionResult(Outcome outcome) {
        this(outcome, 0, null, null, null);
    }

    public ActionResult(Outcome outcome, int timeTaken) {
        this(outcome, timeTaken, null, null, null);
    }

    public ActionResult(Outcome outcome, String word, List<? extends Entity> alternatives) {
        this(outcome, 0, null, word, alternatives);
    }

    public Outcome getOutcome() {
        return outcome;
    }
    
    public boolean isSuccess() {
        return outcome == Outcome.SUCCESS;
    }

    public String getMessage() {
        return message;
    }

    public int getTimeTaken() {
        return timeTaken;
    }
    
    public List<? extends Entity> getAlternatives() {
        return alternatives;
    }

    public int process(Person actor, Entity target, ActionBase action) {
        switch (outcome) {
        case SUCCESS:
            break;
        case NOT_FOUND:
            boolean isName = Character.isUpperCase(word.charAt(0));
            System.out.println(
                    "You do not see " + (isName ? "" : "a ") + word);
            break;
        case NOT_UNIQUE:
            System.out.println("There are multiple matches for " + word);
            int ordinal = 1;
            for (Entity alt: alternatives) {
                System.out.println("  " + word +
                        "#" + ordinal + ": " + alt.getName());
                ordinal++;
            }
            break;
        case ALREADY_DONE:
            System.out.println(
                    Grammar.capitalize(Grammar.definiteName(target)) 
                    + " is already " + 
                    (message == null ? action.getAdjective() : message));
            break;
        case NOT_APPLICABLE:
            System.out.println(
                    Grammar.capitalize(Grammar.definiteName(target)) + 
                    " is not something that you can " + action.getVerb());
            break;
        case NOT_POSSIBLE:
            System.out.println("You failed to " + action.getVerb() + " " + 
                    Grammar.definiteName(target) +
                    ((message == null) ? "" : (" because " + message)));
            break;
        case NOT_ON_SELF:
            System.out.println("You cannot " + action.getVerb() + " yourself");
            break;
        case CANNOT_SEE:
            System.out.println("You cannot do that in the dark.");
            break;
        case SYNTAX:
            System.out.println(message);
            break;
        default:
            throw new AssertionError("Unexpected outcome: " + outcome);
        }
        return timeTaken;
    }

}
