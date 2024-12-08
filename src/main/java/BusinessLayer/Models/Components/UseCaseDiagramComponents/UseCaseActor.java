package BusinessLayer.Models.Components.UseCaseDiagramComponents;

import BusinessLayer.Models.Component;
import BusinessLayer.Models.Point;

import java.io.Serializable;

public class UseCaseActor extends Component implements Serializable {
    //private String name;
    //private Point initial;

    //no usage(may need it?)
//    public UseCaseActor(int id, Point initialPoint, String name) {
//        super(id, initialPoint.getX(), initialPoint.getY());
//        super.setInitialPoint(initialPoint);
//        super.setName(name);
//    }

    public UseCaseActor(int id, Point initial) {
        super(id, initial.getX(), initial.getY());
        super.setInitialPoint(initial);
        super.setName("Actor " + id);
    }

    public Point getInitialPoint() {
        return super.getInitialPoint();
    }

    public void setInitialPoint(Point initial) {
        super.setInitialPoint(initial);
    }

    public String getName() {
        return super.getName();
    }

    public void setName(String name) {
        super.setName(name);
    }


}