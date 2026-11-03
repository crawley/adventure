package adventure.actions;

import adventure.Entity;

/**
 * Methods for massaging text into plausible English.
 */
public class Grammar {

    /**
     * Massage an entity's name (or abbrev) into the definite form.
     * 
     * @param entity the entity to be named.
     * @return the massaged name
     */
    public static String definiteName(Entity entity) {
        String name = entity.getName();
        if (name == null) {
            return "the " + entity.getAbbrev();
        } else if (name.startsWith("a ")) {
            return "the" + name.substring(1, name.length());
        } else if (name.startsWith("an ")) {
            return "the" + name.substring(2, name.length());
        } else {
            return name;
        }
    }

    /**
     * Massage an entity's description into the indefinite form.
     * 
     * @param entity the entity to be described.
     * @return the massaged description
     */
    public static String indefiniteDescription(Entity entity) {
        String desc = entity.getDescription();
        if (desc.startsWith("the ")) {
            String rest = desc.substring(4, desc.length());
            if ("aeiou".contains(desc.substring(0, 1))) {
                return "an " + rest;
            } else {
                return "a " + rest;
            }
        } else {
            return desc;
        }
    }

    /**
     * Capitalize the first word of a phrase or sentence.
     * 
     * @param phrase
     * @return the capitalized phrase
     */
    public static String capitalize(String phrase) {
        if (phrase.length() > 0) {
            return Character.toUpperCase(phrase.charAt(0)) 
                    + phrase.substring(1);
        } else {
            return phrase;
        }
    }

}
