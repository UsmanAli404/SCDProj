package BusinessLayer.Models.Components.UseCaseDiagramComponents;

import BusinessLayer.Models.Component;
import BusinessLayer.Models.Diagrams.ClassDiagram;
import BusinessLayer.Models.Diagrams.UsecaseDiagram;
import BusinessLayer.Models.Point;

import java.io.Serializable;
import java.util.ArrayList;

public class UseCase extends Component implements Serializable {
    //private Point initialPoint;
    //private String name;
    private ArrayList<DependencyRelationship> associatedRelationships;

    public UseCase(int id, Point initialPoint) {
        super(id, initialPoint.getX(), initialPoint.getY());
        super.setInitialPoint(initialPoint);
        super.setName("Use Case");
        this.associatedRelationships = new ArrayList<>();
    }


    //no usage (may need it?)
//    public UseCase(int id, Point initialPoint, String name) {
//        super(id, initialPoint.getX(), initialPoint.getY());
//        super.setInitialPoint(initialPoint);
//        super.setName(name);
//        this.associatedRelationships = new ArrayList<>();
//    }

    //no usage (may need it?)
//    public UseCase(int id, Point initialPoint, String name, ArrayList<DependencyRelationship> associatedRelationships) {
//        super(id, initialPoint.getX(), initialPoint.getY());
//        super.setInitialPoint(initialPoint);
//        super.setName("Use Case");
//        this.associatedRelationships = associatedRelationships;
//    }

    public Point getInitialPoint() {
        return super.getInitialPoint();
    }
    public boolean hasAnyRelationshipWith(UseCase other) {
        return associatedRelationships.stream()
                .anyMatch(rel -> rel.getEndUseCase() == other || rel.getStartUseCase() == other);
    }

    public void setInitialPoint(Point initialPoint) {
        super.setInitialPoint(initialPoint);
    }

    public String getName() {
        return super.getName();
    }

    public void setName(String name) {
        super.setName(name);
    }

    public ArrayList<DependencyRelationship> getAssociatedRelationships() {
        return associatedRelationships;
    }

    public void addAssociatedRelationship(DependencyRelationship relationship) {
        this.associatedRelationships.add(relationship);
    }


}
