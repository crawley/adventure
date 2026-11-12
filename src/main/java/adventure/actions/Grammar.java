package adventure.actions;

import adventure.Entity;

/**
 * Helper methods for massaging text into vaguely plausible English.
 * (I confess, it is string bashing ... not grammar.)
 */
public class Grammar {

    /**
     * Get definite form of entity's name (or abbrev).
     * 
     * @param entity the entity to be named.
     * @return the definite form
     */
    public static String definiteName(Entity entity) {
        String name = (entity.getName() == null) ?
                entity.getAbbrev() : entity.getName();
        return definiteForm(name);
    }

    /**
     * Get indefinite form of entity's name (or abbrev).
     * 
     * @param entity the entity to be named.
     * @return the indefinite form
     */
    public static String indefiniteName(Entity entity) {
        String name = (entity.getName() == null) ?
                entity.getAbbrev() : entity.getName();
        return definiteForm(name);
    }

    /**
     * Massage a phrase into its definite form.
     * 
     * @param phrase a phrase to be massaged.
     * @return the massaged phrase
     */
    public static String definiteForm(String phrase) {
        // TODO - account for existing article capitalization
        if (phrase.startsWith("a ")) {
            return "the" + phrase.substring(1, phrase.length());
        } else if (phrase.startsWith("an ")) {
            return "the" + phrase.substring(2, phrase.length());
        } else {
            return phrase;
        }
    }

    /**
     * Massage an entity's description into the indefinite form.
     * 
     * @param entity the entity to be described.
     * @return the massaged description
     */
    public static String indefiniteDescription(Entity entity) {
        return indefiniteForm(entity.getDescription());
    }

    /**
     * Massage a phrase into the indefinite form.
     * 
     * @param phrase a phrase to be massaged.
     * @return the massaged phrase
     */
    public static String indefiniteForm(String phrase) {
        // TODO - account for existing article capitalization
        if (phrase.startsWith("the ")) {
            String rest = phrase.substring(4, phrase.length());
            if ("aeiou".contains(phrase.substring(0, 1))) {
                return "an " + rest;
            } else {
                return "a " + rest;
            }
        } else {
            return phrase;
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
