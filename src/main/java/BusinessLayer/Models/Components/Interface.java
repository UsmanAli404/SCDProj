package BusinessLayer.Models.Components;

import BusinessLayer.Models.Component;

import java.util.ArrayList;

public class Interface extends Component {
    private int width, height;
    private ArrayList<String> attributes;//each must be unique
    private ArrayList<String> methods;//each one must be unique
    public Interface(int id, int x, int y, int width, int height) {
        super(id, x, y);
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void draw(){

    }
}
