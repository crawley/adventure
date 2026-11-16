package adventure;

import java.util.ArrayList;
import java.util.List;

public class Associations {
    public record Relation (
        String relationship,
        String label1,
        String label2
    ){}
    
    private List<Relation> relations = new ArrayList<>();
    
    public Associations() {}
    
    public void addRelationship(String relationship,
        String label1,
        String label2) {
        relations.add(new Relation(relationship, label1, label2));
    }
    
    /**
     * Test if a relation exists between two items.  Null items are wildcards; e.g.
     * 'isRelated("MOTHER", actor, null)' returns true if there is <i>any</i> "MOTHER"
     * relation for 'actor'.
     * 
     * @param relationship the name of the relationship
     * @param entity1 the first item or null
     * @param entity2 the second item or null
     * @return true iff a relation exists.
     */
    public boolean isRelated(String relationship, Entity entity1, Entity entity2) {
        String label1 = entity1 == null ? null : entity1.getLabel();
        String label2 = entity2 == null ? null : entity2.getLabel();
        for (Relation relation: relations) {
            if (relation.relationship.equals(relationship) &&
                    (label1 == null || relation.label1.equals(label1)) &&
                    (label2 == null || relation.label2.equals(label2))) {
                return true;
            }
        }
        return false;
    }
}
