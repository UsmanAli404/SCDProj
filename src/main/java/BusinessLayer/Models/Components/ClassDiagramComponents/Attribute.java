package BusinessLayer.Models.Components.ClassDiagramComponents;

import java.io.Serializable;

/**
 * Represents a class attribute in a UML class diagram.
 * */
public class Attribute implements Serializable {
    private String name;
    private String dataType;
    private String accessModifier;

    /**
     * Initializes an Attribute with the specified name and data type,
     * and assigns a default access modifier of "public".
     *
     * @param name     The name of the attribute
     * @param dataType The data type of the attribute
     */
    public Attribute(String name, String dataType) {
        this.name = name;
        this.dataType = dataType;
        this.accessModifier = "public";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getAccessModifier() {
        return accessModifier;
    }

    public void setAccessModifier(String accessModifier) {
        this.accessModifier = accessModifier;
    }

    /**
     * Returns a string representation of the attribute in UML format.
     *
     * @return The UML representation of the attribute, including its access modifier, name, and data type
     */
    @Override
    public String toString() {
        return switch (accessModifier.toLowerCase()) {
            case "private" -> "-" + name + " : " + dataType;
            case "public" -> "+" + name + " : " + dataType;
            default -> "#" + name + " : " + dataType;
        };
    }
}
