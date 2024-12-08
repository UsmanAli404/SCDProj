package BusinessLayer.Models;

import java.io.Serializable;

/**
 * Represents a generic component in a UML model.
 * <p>
 * This class serves as a base for specific UML components such as classes, interfaces,
 * and actors. It provides core attributes and behaviors, such as an ID, name, and initial
 * position, to be shared across all UML components.
 * </p>
 *
 * <p>
 * Subclasses under `UseCaseDiagramComponents` and `ClassDiagramComponents` extend
 * this class to implement specialized functionality. This design enables consistent
 * management of UML components in arrays, allowing easy addition, updating,
 * and removal.
 * </p>
 */
public abstract class Component {

    /**
     * A unique identifier for the UML component.
     */
    private int id;

    /**
     * The name of the UML component. Can be used for descriptive or display purposes.
     */
    private String name;

    /**
     * The initial position of the UML component, represented by a `Point` object.
     * This includes x and y coordinates.
     */
    private Point initialPoint;

    /**
     * Constructs a new Component with the specified ID and initial coordinates.
     *
     * @param id The unique identifier for the component.
     * @param x  The x-coordinate of the component's initial position.
     * @param y  The y-coordinate of the component's initial position.
     */
    public Component(int id, double x, double y) {
        this.id = id;
        this.initialPoint = new Point(x, y); // Instantiate the initial position using the Point class.
    }

    /**
     * Retrieves the initial position of the component.
     *
     * @return The `Point` object representing the initial position.
     */
    public Point getInitialPoint() {
        return initialPoint;
    }

    /**
     * Updates the initial position of the component.
     *
     * @param initialPoint A `Point` object representing the new position.
     */
    public void setInitialPoint(Point initialPoint) {
        this.initialPoint = initialPoint;
    }

    /**
     * Retrieves the unique identifier of the component.
     *
     * @return The unique identifier as an integer.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the component.
     *
     * @param id The new unique identifier for the component.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retrieves the x-coordinate of the component's initial position.
     *
     * @return The x-coordinate as a double.
     */
    public double getX() {
        return initialPoint.getX();
    }

    /**
     * Retrieves the y-coordinate of the component's initial position.
     *
     * @return The y-coordinate as a double.
     */
    public double getY() {
        return initialPoint.getY();
    }

    /**
     * Retrieves the name of the component.
     *
     * @return The name as a string.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the component.
     *
     * @param name The new name for the component.
     */
    public void setName(String name) {
        this.name = name;
    }
}
