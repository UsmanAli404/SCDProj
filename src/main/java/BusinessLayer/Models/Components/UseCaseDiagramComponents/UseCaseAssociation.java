package BusinessLayer.Models.Components.UseCaseDiagramComponents;

import BusinessLayer.Models.Component;
import BusinessLayer.Models.Point;

import java.io.Serializable;

public class UseCaseAssociation extends Component implements Serializable {
    private Point start;
    private Point end;
    private Component useCase;
    private Component actor;

    public UseCaseAssociation(int id, Point start, Point end, UseCase useCase, UseCaseActor actor) {
        super(id, start.getX(), start.getY());
        this.start = start;
        this.end = end;
        this.useCase = useCase;
        this.actor = actor;
    }

//    public UseCaseAssociation(int id, Point start, Point end, UseCase useCase) {
//        super(id, start.getX(), start.getY());
//        this.start = start;
//        this.end = end;
//        this.useCase = useCase;
//        this.actor =
//    }


    public Component getActor() {
        return actor;
    }

    public void setActor(UseCaseActor actor) {
        this.actor = actor;
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    public Component getUseCase() {
        return useCase;
    }

    public void setUseCase(UseCase useCase) {
        this.useCase = useCase;
    }


}
