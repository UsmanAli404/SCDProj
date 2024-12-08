package BusinessLayer.Models.Components.ClassDiagramComponents;

import BusinessLayer.Models.Component;
import BusinessLayer.Models.Point;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a class in a UML class diagram.
 */
public class Class extends Component implements Serializable {

    private ArrayList<Attribute> attributes;
    private ArrayList<Function> functions;
    private ArrayList<Class> inheritedClasses;

    /**
     * Initializes a new Class with the given ID and initial point.
     *
     * @param id            The unique identifier for the class
     * @param initialPoint  The initial point for the class in the diagram
     */
    public Class(int id, Point initialPoint){
        super(id, initialPoint.getX(), initialPoint.getY());
        //this.className = "Class "+id;
        super.setName("Class "+id);
        this.attributes = new ArrayList<>();
        this.functions = new ArrayList<>();
        this.inheritedClasses = new ArrayList<>();
        super.setInitialPoint(initialPoint);
    }

    /**
     * Initializes a new Class with the given ID, position, name, and initial point.
     *
     * @param id            The unique identifier for the class
     * @param x             The x-coordinate for the class
     * @param y             The y-coordinate for the class
     * @param className     The name of the class
     * @param initialPoint  The initial point for the class in the diagram
     */
    public Class(int id, int x, int y, String className, Point initialPoint) {
        super(id, x, y); // Call the parent class constructor
        super.setName(className);
        super.setInitialPoint(initialPoint);
        this.attributes = new ArrayList<>();
        this.functions = new ArrayList<>();
        this.inheritedClasses = new ArrayList<>();
    }

    /**
     * Initializes a new Class with the given ID, position, and initial point.
     * The class name is set to "Class" by default.
     *
     * @param id            The unique identifier for the class
     * @param x             The x-coordinate for the class
     * @param y             The y-coordinate for the class
     * @param initialPoint  The initial point for the class in the diagram
     */
    public Class(int id, int x, int y, Point initialPoint) {
        super(id, x, y); // Call the parent class constructor
        super.setInitialPoint(initialPoint);
        this.attributes = new ArrayList<>();
        this.functions = new ArrayList<>();
        super.setName("Class");
        this.inheritedClasses = new ArrayList<>();
    }

    /**
     * Initializes a new Class with the given ID, position, class name,
     * attributes, functions, and initial point.
     *
     * @param id            The unique identifier for the class
     * @param x             The x-coordinate for the class
     * @param y             The y-coordinate for the class
     * @param className     The name of the class
     * @param attributes    The list of attributes for the class
     * @param functions     The list of functions for the class
     * @param initialPoint  The initial point for the class in the diagram
     */
    public Class(int id, int x, int y, String className, ArrayList<Attribute> attributes, ArrayList<Function> functions, Point initialPoint) {
        super(id, x, y); // Call the parent class constructor
        super.setName(className);
        this.attributes = attributes;
        this.functions = functions;
        super.setInitialPoint(initialPoint);
        this.inheritedClasses = new ArrayList<>();
    }

    // Getters and setters
    public String getClassName() {
        return super.getName();
    }

    public void setClassName(String className) {
        super.setName(className);
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(ArrayList<Function> functions) {
        this.functions = functions;
    }

    public Point getInitialPoint() {
        return super.getInitialPoint();
    }

    public void setInitialPoint(Point initialPoint) {
        super.setInitialPoint(initialPoint);
    }

    public ArrayList<Class> getInheritedClasses() {
        return inheritedClasses;
    }

    public void setInheritedClasses(ArrayList<Class> inheritedClasses) {
        this.inheritedClasses = inheritedClasses;
    }

    // Add or remove attributes and functions
    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    public void addFunction(Function function) {
        functions.add(function);
    }

    public void removeFunction(Function function) {
        functions.remove(function);
    }

    public void removeAttribute(Attribute attribute) {
        attributes.remove(attribute);
    }
}
