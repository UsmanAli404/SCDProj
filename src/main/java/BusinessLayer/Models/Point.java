package BusinessLayer.Models;

import java.io.Serializable;

/**
 * Represents a point in a 2D coordinate system.
 *
 * <p>
 * Objects of this class store x and y coordinates, primarily used to manage
 * the positions of elements on the screen. For example:
 * <ul>
 * <li>The initial position of a class element in a class diagram.</li>
 * <li>The start and end points for drawing lines.</li>
 * </ul>
 * </p>
 */
public class Point implements Serializable {

    /**
     * The x-coordinate of the point.
     */
    private Double x;

    /**
     * The y-coordinate of the point.
     */
    private Double y;

    /**
     * Creates a new point with the specified x and y coordinates.
     *
     * @param x The value of the x-coordinate.
     * @param y The value of the y-coordinate.
     */
    public Point(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Retrieves the x-coordinate of the point.
     *
     * @return The x-coordinate as a Double.
     */
    public Double getX() {
        return x;
    }

    /**
     * Updates the x-coordinate of the point.
     *
     * @param x The new value for the x-coordinate.
     */
    public void setX(Double x) {
        this.x = x;
    }

    /**
     * Retrieves the y-coordinate of the point.
     *
     * @return The y-coordinate as a Double.
     */
    public Double getY() {
        return y;
    }

    /**
     * Updates the y-coordinate of the point.
     *
     * @param y The new value for the y-coordinate.
     */
    public void setY(Double y) {
        this.y = y;
    }

    /**
     * Returns a string representation of the point in XML-like format.
     *
     * @return A string in the format: `<X>x_value</X><Y>y_value</Y>`.
     */
    @Override
    public String toString() {
        return "<X>" + x + "</X>" + "<Y>" + y + "</Y>";
    }
}
