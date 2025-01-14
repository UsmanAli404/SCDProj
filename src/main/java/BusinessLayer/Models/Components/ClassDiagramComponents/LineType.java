package BusinessLayer.Models.Components.ClassDiagramComponents;

public enum LineType {
    ASSOCIATION("Association"),
    INHERITANCE("Inheritance"),
    AGGREGATION("Aggregation"),
    COMPOSITION("Composition"),
    DASHED("DashedLine");

    private final String type;

    LineType(String type){
        this.type = type;
    }
    public String getType() {
        return type;
    }
}
