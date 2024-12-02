package BusinessLayer.Models;

import javafx.geometry.Point2D;

public abstract class Component {
    private int id;
    private Point2D startCoords;

    public Component(int id, int x, int y) {
        this.id = id;
        this.startCoords = new Point2D(x, y);
    }

    public Point2D getStartCoords(){
        return startCoords;
    }

    public void setStartCoords(Point2D startCoords){
        this.startCoords = startCoords;
    }

    public void setId(int id){
        this.id=id;
    }

    public int getId() {
        return id;
    }

    public void draw() {}
}
