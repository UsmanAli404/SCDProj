package BusinessLayer.Models.Components;

import BusinessLayer.Models.Component;
import javafx.geometry.Point2D;

public class Line extends Component {
    private LineType type;
    private Component source;
    private Component target;
    private Point2D endCoords;

    public Line(int id, Point2D startCoords, Point2D endCoords, LineType type, Component source, Component target) {
        super(id, (int) startCoords.getX(), (int) startCoords.getY());
        this.endCoords = endCoords;
        this.type = type;
        this.source = source;
        this.target = target;
    }

    public void setType(LineType lineType){
        this.type = lineType;
    }

    public LineType getType() {
        return type;
    }

    public Component getSource() {
        return source;
    }

    public void setSource(Component source) {
        this.source = source;
    }

    public Component getTarget() {
        return target;
    }

    public void setTarget(Component target) {
        this.target = target;
    }

    public Point2D getEndCoords() {
        return endCoords;
    }

    public void setEndCoords(Point2D endCoords) {
        this.endCoords = endCoords;
    }

    public enum LineType {
        ASSOCIATION, INHERITANCE, AGGREGATION, COMPOSITION
    }

    @Override
    public void draw() {

    }
}
