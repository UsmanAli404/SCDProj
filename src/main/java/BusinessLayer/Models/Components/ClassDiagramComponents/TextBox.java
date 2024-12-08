package BusinessLayer.Models.Components.ClassDiagramComponents;

import BusinessLayer.Models.Component;

import java.io.Serializable;

public class TextBox extends Component implements Serializable {
    private String text="";

    public TextBox(int id, double x, double y){
        super(id, x, y);
        super.setName("TextBox "+id);
        this.text = "TextBox "+id;
    }

    public TextBox(int id, double x, double y, String text) {
        super(id, x, y);
        super.setName("TextBox "+id);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
