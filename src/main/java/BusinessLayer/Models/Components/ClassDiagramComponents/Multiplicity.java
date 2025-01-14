package BusinessLayer.Models.Components.ClassDiagramComponents;

public class Multiplicity {
    private String first;
    private String second;
    public Multiplicity(String first, String second){
        this.first = first;
        this.second = second;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }
}
