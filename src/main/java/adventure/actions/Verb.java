package adventure.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adventure.Actor;
import adventure.Container;
import adventure.Entity;
import adventure.Item;

public abstract class Verb implements Action {
    
    protected enum Context {
        ITEMS_WORN, ITEMS_HELD, ITEMS_ON_FLOOR, ITEMS_ON_WALLS, 
        WALLS, PORTALS, 
        ACTORS_HERE, ACTORS_ANYWHERE;
        
        public List<Item> items(Actor actor) {
            return switch (this) {
            case ITEMS_WORN -> actor.getItemsWorn();
            case ITEMS_HELD -> actor.getItemsInHand();
            case ITEMS_ON_FLOOR -> actor.getLocation().getItems();
            case ITEMS_ON_WALLS -> actor.getLocation().wallItems();
            case PORTALS -> actor.getLocation().portalItems();
            default ->
                new ArrayList<Item>();
            };
        }
        
        public List<Actor> actors(Actor actor) {
            return switch (this) {
            case ACTORS_HERE -> actor.getLocation().actors();
            default ->
                new ArrayList<Actor>();
            };
        }
    }

    private final String verb;
    private final String adjective;
    private final String participle;

    public Verb(String verb, String adjective, String participle) {
        this.verb = verb;
        this.adjective = adjective;
        this.participle = participle;
    }

    protected VerbResult apply(Actor actor, Item item) {
        throw new UnsupportedOperationException(
                "apply not supported for " + this.getClass());
    }
        
    protected VerbResult applyWith(Actor actor, Item item, Item item2) {
        throw new UnsupportedOperationException(
                "applyWith not supported for " + this.getClass());
    }

    protected VerbResult applyActor(Actor actor, Actor target) {
        throw new UnsupportedOperationException(
                "applyActor not supported for " + this.getClass());
    }

    protected VerbResult applyActorWith(Actor actor, Actor target, Item item) {
        throw new UnsupportedOperationException(
                "applyActorWith not supported for " + this.getClass());
    }

    /**
     * Perform a one argument verb.  The argument is resolved and should
     * resolve to exactly one item.  If not the outcome is either NOT_FOUND
     * or NOT_UNIQUE. 
     * 
     * @param actor
     * @param word the word denoting the argument
     * @param contexts resolve word in these contexts
     * @return the VerbResult that gives the outcome, etcetera.
     */
    protected VerbResult applyHere(Actor actor, String word, 
            Context ... contexts) {
        List<Item> items = resolve(word, actor, contexts);
        VerbResult res = checkUnique(word, items);
        if (res != null) {
            return res;
        } else {
            return apply(actor, items.get(0));
        }
    }
    
    protected VerbResult applyActor(Actor actor, String word, 
            Context[] contexts) {
        List<Actor> actors = findActor(actor, word, contexts);
        VerbResult res = checkActor(actor, word, actors);
        if (res != null) {
            return res;
        } else {
            return applyActor(actor, actors.get(0));
        }
    }

    private VerbResult checkActor(Actor actor, String word, List<Actor> actors) {
        if (actors.isEmpty()) {
            return new VerbResult(VerbResult.Outcome.NOT_FOUND, word);
        } else if (actors.size() > 1) {
            return new VerbResult(VerbResult.Outcome.NOT_UNIQUE, word, actors);
        } else if (actors.contains(actor)) {
            return new VerbResult(VerbResult.Outcome.NOT_ON_SELF, word);
        } else {
            return null;
        }
    }

    private List<Actor> findActor(Actor actor, String word,  
            Context[] contexts) {
        if (contexts.length > 1 || contexts[0] != Context.ACTORS_HERE) {
            throw new UnsupportedOperationException("these contexts");
        }
        if (word.contains("#")) {
            throw new UnsupportedOperationException("# in actor designator");
        }
        return actor.getLocation().actors().stream()
                .filter((a) -> (a.getAbbrev().equals(word)
                        || a.getName().equals(word)))
                .toList();
    }

    /**
     * Perform a two argument verb.  The arguments are resolved and should
     * resolve to exactly one item.  If not the outcome is either NOT_FOUND
     * or NOT_UNIQUE. 
     * 
     * If fromContainer is false, word is resolved in the given contexts and
     * word2 in Context.HELD.  If fromContainer is false, word2 is resolved in 
     * the given contexts to give a container and word is resolved in the container.
     * 
     * @param actor
     * @param word the first argument word
     * @param word2 the second argument word
     * @param fromContainer determines resolution logic
     * @param resolve word or word2 in these contexts
     * @return the VerbResult that gives the outcome, etcetera.
     */
    protected VerbResult applyHereWith(Actor actor, String word, String word2,
            boolean fromContainer, Context ... contexts) {
        List<Item> items = null;
        List<Item> item2s = null;
        VerbResult res;
        if (fromContainer) {
            item2s = resolve(word2, actor, contexts);
            res = checkUnique(word2, item2s);
            if (res == null) {
                items = resolve(word, actor, item2s);
                res = checkUnique(word, items);
            }
        } else {
            items = resolve(word, actor, contexts);
            res = checkUnique(word, items);
            if (res == null) {
                item2s = resolve(word2, actor, Context.ITEMS_HELD);
                res = checkUnique(word2, item2s);
            }
        }
        if (res != null) {
            return res;
        } else {
            return applyWith(actor, items.get(0), item2s.get(0));
        }
    }

    private VerbResult checkUnique(String word, List<Item> items) {
        if (items.isEmpty()) {
            return new VerbResult(VerbResult.Outcome.NOT_FOUND, word);
        } else if (items.size() > 1) {
            return new VerbResult(VerbResult.Outcome.NOT_UNIQUE, word, items);
        } else {
            return null;
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
    @SafeVarargs
    public static List<Item> resolve(String word, Actor actor, Context ... contexts) {
        List<List<Item>> lists = Arrays.stream(contexts)
                .map((c) -> c.items(actor))
                .toList();
        return doResolve(word, lists);
    }

    public static List<Item> resolve(String word, Actor actor, List<Item> list) {
        List<List<Item>> lists = list.stream()
                .filter((item) -> item instanceof Container)
                .map((item) -> ((Container) item).getContents())
                .toList();
        return doResolve(word, lists);
    }

    private static List<Item> doResolve(String word, List<List<Item>> lists) {
        String[] parts = word.split("#", 2);
        List<Item> filtered = lists.stream()
                .flatMap((l) -> l.stream())
                .filter((i) -> i.getAbbrev().equals(parts[0]))
                .filter((i) -> !i.isHidden())
                .toList();
        if (parts.length == 2) {
            List<Item> res = new ArrayList<>();
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

    protected int performOnItem(Actor actor, String[] args, 
            Context ...contexts) {
        if (args.length == 0) {
            System.out.println("What do you want to " + verb + "?");
            return 0;
        } else if (args.length > 1) {
            System.out.println("You can only " + 
                    verb + " one thing at a time");
            return 0;
        } else {
            return processVerbResult(applyHere(actor, args[0], contexts));
        }
    }
    
    protected int performOnActor(Actor actor, String[] args, 
            Context ...contexts) {
        if (args.length == 0) {
            System.out.println("Who or what do you want to " + verb + "?");
            return 0;
        } else if (args.length > 1) {
            System.out.println(
                    "You can only " + verb + " one being at a time");
            return 0;
        } else {
            return processVerbResult(applyActor(actor, args[0], contexts));
        }
    }

    protected int performOnItemWith(Actor actor, String[] args,
            boolean fromContainer, String preposition, Context ...contexts) {
        if (args.length == 0) {
            System.out.println("What do you want to " + verb + "?");
        } else {
            String label = args[0];
            String label2 = null;
            if (args.length == 1) {
                System.out.println("What do you want to " + 
                        verb + " " + args[0] + " " + preposition +"?");
            } else if (args[1].equals(preposition)) {
                if (args.length == 2) {
                    System.out.println(verb + " " + args[0] + " " 
                            + preposition + " what?");
                } else {
                    label2 = args[2];
                }
            } else {
                label2 = args[1];
            }
            if (label2 != null) {
                VerbResult res = applyHereWith(
                        actor, label, label2, fromContainer, contexts);
                return processVerbResult(res);
            }
        }
        return 0;
    }
    
    private int processVerbResult(VerbResult res) {
        Actor actor = res.getActor();
        Item item = res.getItem();
        Item item2 = res.getItem2();
        Entity target = (actor == null) ? item : actor;
        switch (res.getOutcome()) {
        case SUCCESS:
            if (target instanceof Item) {
                onSuccess(item, item2);
            } else {
                onSuccess(actor, item);
            }
            break;
        case NOT_FOUND:
            boolean isName = Character.isUpperCase(res.getWord().charAt(0));
            System.out.println(
                    "You do not see " + (isName ? "" : "a ") + res.getWord());
            break;
        case NOT_UNIQUE:
            System.out.println("There are multiple matches for " + res.getWord());
            int ordinal = 1;
            for (Entity alt: res.getAlternatives()) {
                System.out.println("  " + res.getWord() +
                        "#" + ordinal + ": " + alt.getName());
                ordinal++;
            }
            break;
        case ALREADY_DONE:
            System.out.println(target.getName() + " is already " + adjective);
            break;
        case NOT_APPLICABLE:
            System.out.println(target.getName() + 
                    " is not something that you can " + verb);
            break;
        case NOT_POSSIBLE:
            System.out.println("You failed to " + verb + " " + 
                    target.getName() +
                    ((res.getDetails() == null) ? "" : 
                        (" because " + res.getDetails())));
            break;
        case NOT_ON_SELF:
            System.out.println("You cannot " + verb + " yourself");
            break;
        default:
            break;
        }
        return res.getTimeTaken();
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

    protected void onSuccess(Item item, Item item2) {
    }
    
    protected void onSuccess(Actor actor, Item item) {
        
    }

}
