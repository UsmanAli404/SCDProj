package BusinessLayer.Models.Components.ClassDiagramComponents;

import BusinessLayer.Models.Component;
import BusinessLayer.Models.Point;

import java.io.Serializable;
import java.util.ArrayList;

public class Interface extends Component implements Serializable {

    private ArrayList<Function> functions;
    private ArrayList<Class> inheritedClasses;

    public Interface(int id, Point initialPoint){
        super(id, initialPoint.getX(), initialPoint.getY());
        super.setName("Interface "+id);
        this.functions = new ArrayList<>();
        this.inheritedClasses = new ArrayList<>();
    }

    public Interface(int id, double x, double y, String className) {
        super(id, x, y); // Call the parent class constructor
        super.setName(className);
        this.functions = new ArrayList<>();
        this.inheritedClasses = new ArrayList<>();
    }

    public Interface(int id, double x, double y) {
        super(id, x, y); // Call the parent class constructor
        super.setName("Interface "+id);
        this.functions = new ArrayList<>();
        this.inheritedClasses = new ArrayList<>();
        super.setInitialPoint(new Point(x,y));
    }

    public String getClassName() {
        return super.getName();
    }

    public void setClassName(String className) {
        super.setName(className);
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    public Point getInitialPoint() {
        return super.getInitialPoint();
    }

    public void setInitialPoint(Point initialPoint) {
        super.setInitialPoint(initialPoint);
    }


    public void setFunctions(ArrayList<Function> functions) {
        this.functions = functions;
    }

    public ArrayList<Class> getInheritedClasses() {
        return inheritedClasses;
    }

    public void setInheritedClasses(ArrayList<Class> inheritedClasses) {
        this.inheritedClasses = inheritedClasses;
    }

    public void addFunction(Function function) {
        functions.add(function);
    }

    public void removeFunction(Function function) {
        functions.remove(function);
    }

    //no usage (may need it?)
//    public String returnFunction() {
//        StringBuilder result = new StringBuilder();
//        for (Function function : functions) {
//            result.append(function.toString()).append("\n");
//        }
//        return result.toString();
//    }

    public String generateCode() {
        StringBuilder code = new StringBuilder();

        // Add interface declaration
        code.append("interface ").append(super.getName()).append(" {\n");

        // Add functions as abstract method signatures
        for (Function function : functions) {
            code.append("    ").append(function.toString()).append(";\n");
        }

        // Close interface definition
        code.append("}");

        return code.toString();
    }
}
