package BusinessLayer.Models.Components.ClassDiagramComponents;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Represents a function (method) in a UML class diagram.
 */
public class Function implements Serializable {
    String name;
    String returnType;
    ArrayList<Attribute> attributes;
    String accessModifier;

    /**
     * Initializes a function with a specified return type and name.
     * The attributes list is initialized as empty, and the access modifier is set to "public".
     *
     * @param returnType The return type of the function
     * @param name       The name of the function
     */
    public Function(String returnType, String name) {
        this.returnType = returnType;
        this.name = name;
        attributes = new ArrayList<>();
        accessModifier = "public";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    public void removeAttribute(Attribute attribute) {
        attributes.remove(attribute);
    }

    public String getAccessModifier() {
        return accessModifier;
    }

    public void setAccessModifier(String accessModifier) {
        this.accessModifier = accessModifier;
    }

    /**
     * Returns a string representation of the function in UML format.
     *
     * @return The UML representation of the function, including its access modifier, name, parameters, and return type
     */
    @Override
    public String toString() {
        String attributesString = attributes.stream()
                .map(attr -> attr.getName() + " " + attr.getDataType())
                .collect(Collectors.joining(", "));
        String prefix;
        switch (accessModifier.toLowerCase()) {
            case "private":
                prefix = "-";
                break;
            case "public":
                prefix = "+";
                break;
            default:
                prefix = "#";
        }

        return prefix + name + "(" + attributesString + ") : " + returnType;
    }
}
