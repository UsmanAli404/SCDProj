package BusinessLayer.Models.Components;

import BusinessLayer.Models.Component;

public class Line extends Component {
    private LineType type;
    private Component source;
    private Component target;

    public Line(String id, LineType type, Component source, Component target) {
        super(id);
        this.type = type;
        this.source = source;
        this.target = target;
    }

    public LineType getType() {
        return type;
    }

    @Override
    public void draw() {

    }

    public Component getSource() {
        return source;
    }

    public void setSource(Component source) {
        this.source = source;
    }

    public Component getTarget() {
        return target;
    }

    public void setTarget(Component target) {
        this.target = target;
    }

    public enum LineType {
        ASSOCIATION, INHERITANCE, AGGREGATION, COMPOSITION
    }
}
