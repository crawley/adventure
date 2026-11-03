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
    
    public boolean isRelated(String relationship, Entity item1, Entity item2) {
        String label1 = item1.getLabel();
        String label2 = item2.getLabel();
        for (Relation relation: relations) {
            if (relation.relationship.equals(relationship) &&
                    relation.label1.equals(label1) &&
                    relation.label2.equals(label2)) {
                return true;
            }
        }
        return false;
    }
}
