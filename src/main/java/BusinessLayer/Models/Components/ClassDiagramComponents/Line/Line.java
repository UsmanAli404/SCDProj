package BusinessLayer.Models.Components.ClassDiagramComponents.Line;
import BusinessLayer.Models.Component;
import java.io.Serializable;
import java.util.Objects;

public class Line extends Component implements Serializable {
    private Multiplicity startMultiplicity;
    private Multiplicity endMultiplicity;
    private LineType type;
    private Component startComp;
    private Component endComp;
    public Line(int id, double x, double y, Multiplicity startMultiplicity, Multiplicity endMultiplicity, LineType type, Component startComp, Component endComp){
        super(id, x, y);
        super.setName(type.getType() + id);

        this.startMultiplicity = startMultiplicity;
        this.endMultiplicity = endMultiplicity;
        this.type = type;
        this.startComp = startComp;
        this.endComp = endComp;
    }

    public Line(int id, double x, double y, LineType type, Component startComp, Component endComp){
        super(id, x, y);
        if(Objects.equals(type.getType(), "DashedLine")){
            super.setName("");
            this.startMultiplicity = new Multiplicity("", "");
            this.endMultiplicity = new Multiplicity("", "");
        } else {
            super.setName(type.getType() + id);
            this.startMultiplicity = new Multiplicity("1", "1");
            this.endMultiplicity = new Multiplicity("1", "1");
        }

        this.type = type;
        this.startComp = startComp;
        this.endComp = endComp;
    }

    public Component getStartComp(){
        return startComp;
    }

    public void setStartComp(Component startComp){
        this.startComp = startComp;
    }

    public Component getEndComp(){
        return endComp;
    }

    public void setEndComp(Component endComp){
        this.endComp = endComp;
    }

    public Multiplicity getStartMultiplicity(){
        return startMultiplicity;
    }

    public void setStartMultiplicity(Multiplicity startMultiplicity){
        this.startMultiplicity = startMultiplicity;
    }

    public Multiplicity getEndMultiplicity(){
        return endMultiplicity;
    }

    public void setEndMultiplicity(Multiplicity endMultiplicity){
        this.endMultiplicity = endMultiplicity;
    }

    public String getType(){
        return this.type.getType();
    }

    public void setType(LineType type){
        this.type = type;
    }
}
