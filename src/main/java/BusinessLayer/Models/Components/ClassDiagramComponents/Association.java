package BusinessLayer.Models.Components.ClassDiagramComponents;

import BusinessLayer.Models.Component;
import javafx.scene.shape.Line;

import java.io.Serializable;

/**
 * Represents an association component in a class diagram.
 *
 * <p>
 * An association is a relationship between two classes in a class diagram, where one class
 * is associated with another. This relationship is typically visualized as a line connecting
 * the two classes, with multiplicity markers at both ends to indicate how many instances
 * of one class can be associated with instances of another class.
 * </p>
 */
public class Association extends Component implements Serializable {
    private String startInitialMultiplicity="";
    private String startEndMultiplicity="";
    private String endStartMultiplicity="";
    private String endEndMultiplicity="";
    private Component startClass;
    private Component endClass;
    private Line line;

    /**
     * Initializes an Association object with specified start and end classes and their positions.
     *
     * @param id           Component ID, use ClassDiagram.getUpcomingComponentID() for safety
     * @param x            X-coordinate for the association's position
     * @param y            Y-coordinate for the association's position
     * @param startClass   The starting class of the association
     * @param endClass     The ending class of the association
     */
    public Association(int id, double x, double y, Component startClass, Component endClass) {
        super(id, x, y); // Call the parent class constructor
        super.setName("Association "+id);
        this.startClass = startClass;
        this.endClass = endClass;
        this.line = new Line();
        updateLine();
    }

    /**
     * Initializes an Association object with specified multiplicities, name, and line.
     *
     * @param id                    Component ID
     * @param x                     X-coordinate
     * @param y                     Y-coordinate
     * @param startInitialMultiplicity The initial multiplicity at the start class
     * @param startEndMultiplicity    The end multiplicity at the start class
     * @param endStartMultiplicity   The start multiplicity at the end class
     * @param endEndMultiplicity     The end multiplicity at the end class
     * @param name                   The name of the association
     * @param startClass             The starting class of the association
     * @param endClass               The ending class of the association
     * @param line                   The line representing the association
     */
    public Association(int id, int x, int y, String startInitialMultiplicity, String startEndMultiplicity, String endStartMultiplicity, String endEndMultiplicity, String name, Class startClass, Class endClass, Line line) {
        super(id, x, y); // Call the parent class constructor
        super.setName(name);
        this.startInitialMultiplicity = startInitialMultiplicity;
        this.startEndMultiplicity = startEndMultiplicity;
        this.endStartMultiplicity = endStartMultiplicity;
        this.endEndMultiplicity = endEndMultiplicity;
        this.startClass = startClass;
        this.endClass = endClass;
        this.line = line;
    }

    /**
     * Initializes an Association object with specified multiplicities and line.
     *
     * @param id                    Component ID
     * @param x                     X-coordinate
     * @param y                     Y-coordinate
     * @param startInitialMultiplicity The initial multiplicity at the start class
     * @param startEndMultiplicity    The end multiplicity at the start class
     * @param endStartMultiplicity   The start multiplicity at the end class
     * @param endEndMultiplicity     The end multiplicity at the end class
     * @param startClass             The starting class of the association
     * @param endClass               The ending class of the association
     * @param line                   The line representing the association
     */
    public Association(int id, int x, int y, String startInitialMultiplicity, String startEndMultiplicity, String endStartMultiplicity, String endEndMultiplicity, Class startClass, Class endClass, Line line) {
        super(id, x, y); // Call the parent class constructor
        super.setName("Association "+id);
        this.startInitialMultiplicity = startInitialMultiplicity;
        this.startEndMultiplicity = startEndMultiplicity;
        this.endStartMultiplicity = endStartMultiplicity;
        this.endEndMultiplicity = endEndMultiplicity;
        this.startClass = startClass;
        this.endClass = endClass;
        this.line = line;
    }

    /**
     * Updates the line's coordinates based on the start and end classes' positions.
     */
    public void updateLine() {
        if (startClass != null && endClass != null) {
            line.setStartX(startClass.getInitialPoint().getX());
            line.setStartY(startClass.getInitialPoint().getY());
            line.setEndX(endClass.getInitialPoint().getX());
            line.setEndY(endClass.getInitialPoint().getY());
        }
    }

    // Getters and setters

    public Component getStartClass() {
        return startClass;
    }

    public void setStartClass(Class startClass) {
        this.startClass = startClass;
    }

    public Component getEndClass() {
        return endClass;
    }

    public void setEndClass(Class endClass) {
        this.endClass = endClass;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public String getStartInitialMultiplicity() {
        return startInitialMultiplicity;
    }

    public void setStartInitialMultiplicity(String startInitialMultiplicity) {
        this.startInitialMultiplicity = startInitialMultiplicity;
    }

    public String getStartEndMultiplicity() {
        return startEndMultiplicity;
    }

    public void setStartEndMultiplicity(String startEndMultiplicity) {
        this.startEndMultiplicity = startEndMultiplicity;
    }

    public String getEndStartMultiplicity() {
        return endStartMultiplicity;
    }

    public void setEndStartMultiplicity(String endStartMultiplicity) {
        this.endStartMultiplicity = endStartMultiplicity;
    }

    public String getEndEndMultiplicity() {
        return endEndMultiplicity;
    }

    public void setEndEndMultiplicity(String endEndMultiplicity) {
        this.endEndMultiplicity = endEndMultiplicity;
    }
}
