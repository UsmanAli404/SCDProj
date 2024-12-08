package BusinessLayer.Models.Components.ClassDiagramComponents;

import BusinessLayer.Models.Component;

import java.io.Serializable;
import javafx.scene.shape.Line;

/**
 * Represents a dashed line between two components in a UML class diagram.
 */
public class DashedLine extends Component implements Serializable{
    private Component startClass;
    private Component endClass;
    private Line line;

    /**
     * Initializes a new DashedLine with the given ID, position, and start/end components.
     *
     * @param id            The unique identifier for the dashed line
     * @param x             The x-coordinate for the dashed line
     * @param y             The y-coordinate for the dashed line
     * @param startClass    The starting component of the dashed line
     * @param endClass      The ending component of the dashed line
     */
    public DashedLine(int id, double x, double y, Component startClass, Component endClass) {
        super(id, x, y);
        super.setName("DashedLine "+id);
        this.startClass = startClass;
        this.endClass = endClass;
        this.line = new Line();
        updateLine();
    }

    /**
     * Updates the coordinates of the line based on the positions of the start and end components.
     */
    public void updateLine() {
        if (startClass != null && endClass != null) {
            line.setStartX(startClass.getInitialPoint().getX());
            line.setStartY(startClass.getInitialPoint().getY());
            line.setEndX(endClass.getInitialPoint().getX());
            line.setEndY(endClass.getInitialPoint().getY());
        }
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Component getStartClass() {
        return startClass;
    }

    public void setStartClass(Component startClass) {
        this.startClass = startClass;
    }

    public Component getEndClass() {
        return endClass;
    }

    public void setEndClass(Component endClass) {
        this.endClass = endClass;
    }
}
