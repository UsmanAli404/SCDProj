package BusinessLayer.Models.Components.ClassDiagramComponents;

import BusinessLayer.Models.Component;
import javafx.scene.shape.Line;

import java.io.Serializable;

/**
 * Represents an inheritance component in a class diagram.
 *
 * <p>
 * An inheritance is a relationship between two classes where one class (the subclass)
 * inherits the properties and behaviors of another class (the superclass). It is visualized
 * as a line connecting the two classes, typically with multiplicity markers at both ends.
 * In a class diagram, the inheritance relationship is usually represented by a solid line
 * with an arrowhead pointing from the subclass to the superclass.
 * </p>
 */
public class Inheritance extends Component implements Serializable {
    private String startInitialMultiplicity="";
    private String startEndMultiplicity="";
    private String endStartMultiplicity="";
    private String endEndMultiplicity="";
    private Component startClass;
    private Component endClass;
    private Line line;

    /**
     * Initializes an Inheritance object with specified classes and position.
     *
     * @param id           Component ID, use ClassDiagram.getUpcomingComponentID() for safety
     * @param x            X-coordinate for the inheritance's position
     * @param y            Y-coordinate for the inheritance's position
     * @param startClass   The starting class (subclass) of the inheritance
     * @param endClass     The ending class (superclass) of the inheritance
     */
    public Inheritance(int id, double x, double y, Component startClass, Component endClass) {
        super(id, x, y);
        this.setName("Inheritance "+id);
        this.startClass = startClass;
        this.endClass = endClass;
        this.line = new Line();
        updateLine();
    }

    /**
     * Initializes an Inheritance object with specified multiplicities and line.
     *
     * @param id                    Component ID
     * @param x                     X-coordinate
     * @param y                     Y-coordinate
     * @param startInitialMultiplicity The initial multiplicity at the start class
     * @param startEndMultiplicity    The end multiplicity at the start class
     * @param endStartMultiplicity   The start multiplicity at the end class
     * @param endEndMultiplicity     The end multiplicity at the end class
     * @param startClass             The starting class (subclass) of the inheritance
     * @param endClass               The ending class (superclass) of the inheritance
     * @param line                   The line object representing the inheritance
     */
    public Inheritance(int id, double x, double y, String startInitialMultiplicity, String startEndMultiplicity, String endStartMultiplicity, String endEndMultiplicity, Class startClass, Class endClass, Line line) {
        super(id, x, y);
        this.setName("Inheritance "+id);
        this.startInitialMultiplicity = startInitialMultiplicity;
        this.startEndMultiplicity = startEndMultiplicity;
        this.endStartMultiplicity = endStartMultiplicity;
        this.endEndMultiplicity = endEndMultiplicity;
        this.startClass = startClass;
        this.endClass = endClass;
        this.line = line;
    }

    /**
     * Initializes an Inheritance object with a custom name and multiplicities.
     *
     * @param id                     Component ID
     * @param x                      X-coordinate
     * @param y                      Y-coordinate
     * @param startInitialMultiplicity  Initial multiplicity at the start class
     * @param startEndMultiplicity      End multiplicity at the start class
     * @param endStartMultiplicity     Start multiplicity at the end class
     * @param endEndMultiplicity       End multiplicity at the end class
     * @param name                    Custom name for the inheritance
     * @param startClass              The starting class (subclass)
     * @param endClass                The ending class (superclass)
     * @param line                    The line object representing the inheritance
     */
    public Inheritance(int id, double x, double y, String startInitialMultiplicity, String startEndMultiplicity, String endStartMultiplicity, String endEndMultiplicity, String name, Class startClass, Class endClass, Line line) {
        super(id, x, y);
        this.setName(name);
        this.startInitialMultiplicity = startInitialMultiplicity;
        this.startEndMultiplicity = startEndMultiplicity;
        this.endStartMultiplicity = endStartMultiplicity;
        this.endEndMultiplicity = endEndMultiplicity;
        this.startClass = startClass;
        this.endClass = endClass;
        this.line = line;
    }

    /**
     * Updates the line representing the inheritance, adjusting the start and end points
     * based on the positions of the start and end classes.
     */
    public void updateLine() {
        if (startClass != null && endClass != null) {
            line.setStartX(startClass.getInitialPoint().getX());
            line.setStartY(startClass.getInitialPoint().getY());
            line.setEndX(endClass.getInitialPoint().getX());
            line.setEndY(endClass.getInitialPoint().getY());
        }
    }

    public Component getStartClass() {
        return startClass;
    }

    public Component getEndClass() {
        return endClass;
    }

    public void setEndClass(Class endClass) {
        this.endClass = endClass;
    }

    public void setStartClass(Class startClass) {
        this.startClass = startClass;
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
