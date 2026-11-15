package adventure.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import adventure.Actor;
import adventure.Container;
import adventure.Direction;
import adventure.Entity;
import adventure.Item;
import adventure.Person;
import adventure.actions.ActionResult.Outcome;

public abstract class ActionBase implements Action {

    protected enum Context {
        ITEMS_WORN, ITEMS_HELD, ITEMS_ON_FLOOR, ITEMS_ON_WALLS, 
        WALLS, PORTALS, 
        ACTORS_HERE, ACTORS_ANYWHERE;
        
        public List<Item> items(Person actor) {
            switch (this) {
            case ITEMS_WORN: 
                return actor.getItemsWorn();
            case ITEMS_HELD: 
                return actor.getItemsInHand();
            case ITEMS_ON_FLOOR: 
                return actor.canSee() ? 
                    actor.getLocation().getItems() : Collections.emptyList();
            case ITEMS_ON_WALLS: 
                return actor.canSee() ? 
                    actor.getLocation().wallItems() : Collections.emptyList();
            case PORTALS: 
                return actor.canSee() ? 
                    actor.getLocation().portalItems() : Collections.emptyList();
            default: 
                return Collections.emptyList();
            }
        }
        
        public List<Actor> actors(Actor actor) {
            switch (this) {
            case ACTORS_HERE:
                return actor.getLocation().actors();
            default:
                return Collections.emptyList();
            }
        }
    }
    
    private final String verb;
    private final String adjective;
    private final String participle;

    public ActionBase(String verb, String adjective, String participle) {
        this.verb = verb;
        this.adjective = adjective;
        this.participle = participle;
    }

    public String getVerb() {
        return verb;
    }

    public String getAdjective() {
        return adjective;
    }

    public String getParticiple() {
        return participle;
    }
    
    protected String[] parse1(String[] args) 
            throws SyntaxException {
        if (args.length == 0) {
            throw new SyntaxException("What do you want to " + verb + "?");
        } else if (args.length == 1) {
            return args;
        } else {
            throw new SyntaxException("Too many arguments for " + verb + ".");
        }
    }

    protected String[] parse2(String[] args, String keyword) 
            throws SyntaxException {
        if (args.length == 0) {
            throw new SyntaxException("What do you want to " + verb + "?");
        } else if (args.length == 1) {
            if (args[0].equals(keyword)) {
                throw new SyntaxException("What do you want to " + verb + "?");
            } else {
                throw new SyntaxException("What do you want to " 
                        + verb + " the " + args[0] + " " + keyword + "?");
            }
        } else if (args.length == 2) {
            if (args[1].equals(keyword)) {
                throw new SyntaxException("What do you want to " 
                        + verb + " the " + args[0] + " " + keyword + "?");
            } else {
                return args;
            }
        } else if (args.length == 3 && args[1].equals(keyword)) {
            return new String[] {args[0], args[2]};
        }
        throw new SyntaxException("Too many arguments for " + verb + ".");
    }
    
    protected Direction parseDirection(String word) throws SyntaxException {
        try {
            return Direction.valueOf(word);
        } catch (IllegalArgumentException ex) {
            throw new SyntaxException(word + " is not a direction");
        }
    }

    protected Item resolveItem(Person actor, String word, Context ...contexts)
            throws SyntaxException {
        List<Item> items = resolveItems(word, actor, contexts);
        return checkUnique(actor, items, word);
    }
    
    protected <T extends Entity> T checkUnique(Actor actor, 
            List<T> things, String word)
            throws SyntaxException {
        if (things.isEmpty()) {
            if (actor.canSee()) {
                throw new SyntaxException(
                        new ActionResult(Outcome.NOT_FOUND, 0, null, word, null));
            } else {
                throw new SyntaxException(new ActionResult(Outcome.CANNOT_SEE));
            }
        } else if (things.size() > 1) {
            throw new SyntaxException(
                    new ActionResult(Outcome.NOT_UNIQUE, word, things));
        } else {
            return things.get(0);
        }
    }

    /**
     * Resolve an item designator.  This is either an item abbreviation
     * or an abbreviation and a numeric ordinal separated by a '#' character.
     * 
     * In the former case, the result is all items in the lists that have the
     * given abbreviation.
     * 
     * In the latter case, the result is the nth element of the filtered list, 
     * where 1 denoting the first element, etc.  If the ordinal part is 
     * empty, non-numeric or out of range, an empty list is returned. 
     * 
     * @param word A word denoting one or more items to processed.
     * @param list One or more item lists to resolve the item
     * @return A list of the items that 'word' resolved to.
     */
    public static List<Item> resolveItems(
            String word, Person actor, Context ...contexts ) {
        List<List<Item>> lists = Arrays.stream(contexts)
                .map((c) -> c.items(actor))
                .toList();
        return doResolve(word, lists);
    }

    public static List<Item> resolveItemsInContainers(
            String word, Person actor, List<Entity> list) {
        List<List<Item>> lists = list.stream()
                .filter((item) -> item instanceof Container)
                .map((item) -> ((Container) item).getContents())
                .toList();
        return doResolve(word, lists);
    }

    private static <T extends Entity> List<T> doResolve(
            String word, List<List<T>> lists) {
        String[] parts = word.split("#", 2);
        List<T> filtered = lists.stream()
                .flatMap((l) -> l.stream())
                .filter((i) -> i.getAbbrev().equals(parts[0]))
                .filter((i) -> !i.isHidden())
                .toList();
        if (parts.length == 2) {
            List<T> res = new ArrayList<>();
            try {
                int ordinal = Integer.parseInt(parts[1]);
                if (ordinal >= 1 && ordinal <= filtered.size()) {
                    res.add(filtered.get(ordinal - 1));
                }
            } catch (NumberFormatException ex) {
                // continue
            }
            return res;
        } else {
            return filtered;
        }
    }

}
