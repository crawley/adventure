package adventure.actions;

import adventure.Entity;

/**
 * Methods for massaging text into plausible English.
 */
public class Grammar {

    /**
     * Massage an entity's name (or abbrev) into the definite form.
     * 
     * @param entity the entity to be name.
     * @return the massaged name
     */
    public static String definite(Entity entity) {
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

}
