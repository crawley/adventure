package adventure.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adventure.Actor;
import adventure.Item;

public abstract class Verb implements Action {
    
    protected enum Context {
        WORN, HELD, HERE, PORTALS;
        
        public List<Item> items(Actor actor) {
            return switch (this) {
            case WORN -> actor.getItemsWorn();
            case HELD -> actor.getItemsInHand();
            case HERE -> actor.getCurrentLocation().getItems();
            case PORTALS -> actor.getCurrentLocation().portalItems();
            default ->
                new ArrayList<Item>();
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

    protected VerbResult applyHere(Actor actor, String word, 
            Context ... contexts) {
        List<Item> items = resolve(word, actor, contexts);
        if (items.isEmpty()) {
            return new VerbResult(VerbResult.Outcome.NOT_FOUND, word);
        } else if (items.size() > 1) {
            return new VerbResult(VerbResult.Outcome.NOT_UNIQUE, word, items);
        } else {
            return apply(actor, items.get(0));
        }
    }

    protected VerbResult applyHereWith(Actor actor, String word, String word2,
            Context ... contexts) {
        List<Item> items = resolve(word, actor, contexts);
        if (items.isEmpty()) {
            return new VerbResult(VerbResult.Outcome.NOT_FOUND, word);
        } else if (items.size() > 1) {
            return new VerbResult(VerbResult.Outcome.NOT_UNIQUE, word, items);
        }
        List<Item> item2s = resolve(word2, actor, Context.HELD);
        if (item2s.isEmpty()) {
            return new VerbResult(VerbResult.Outcome.NOT_FOUND, word2);
        } else if (item2s.size() > 1) {
            return new VerbResult(VerbResult.Outcome.NOT_UNIQUE, word2, item2s);
        }
        return applyWith(actor, items.get(0), item2s.get(0));
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

    protected void performOnItem(Actor actor, String[] args, 
            Context ...contexts) {
        if (args.length == 0) {
            System.out.println("What do you want to " + verb + "?");
        } else if (args.length > 1) {
            System.out.println("You can only " + 
                    verb + " one thing at a time");
        } else {
            processVerbResult(applyHere(actor, args[0], contexts));
        }
    }
    
    protected void performOnItemWith(Actor actor, String[] args, 
            String preposition, Context ...contexts) {
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
                VerbResult res = applyHereWith(actor, label, label2, contexts);
                processVerbResult(res);
            }
        }
    }

    private void processVerbResult(VerbResult res) {
        Item item = res.getItem();
        switch (res.getOutcome()) {
        case SUCCESS:
            onSuccess(item); 
            break;
        case NOT_FOUND:
            System.out.println("You do not see " + res.getWord());
            break;
        case NOT_UNIQUE:
            System.out.println("Multiple items match " + res.getWord());
            int ordinal = 1;
            for (Item alt: res.getAlternatives()) {
                System.out.println("  " + res.getWord() +
                        "#" + ordinal + ": " + alt.getName());
                ordinal++;
            }
            break;
        case ALREADY_DONE:
            System.out.println(item.getName() + " is already " + adjective);
            break;
        case NOT_APPLICABLE:
            System.out.println(item.getName() + 
                    " is not something that can be " + participle);
            break;
        case NOT_POSSIBLE:
            System.out.println("You failed to " + verb + " " + 
                    item.getName() +
                    ((res.getDetails() == null) ? "" : 
                        (" because " + res.getDetails())));
            break;
        default:
            break;
        }
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

    protected abstract void onSuccess(Item item);
}
