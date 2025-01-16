package BusinessLayer.Models;

public abstract class Component {
    private int id;
    private String name;
    private Point initialPoint;

    public Component(int id, double x, double y) {
        this.id = id;
        this.initialPoint = new Point(x, y);
    }

    public Point getInitialPoint() {
        return initialPoint;
    }

    public void setInitialPoint(Point initialPoint) {
        this.initialPoint = initialPoint;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getX() {
        return initialPoint.getX();
    }

    public void setX(double x){
        this.initialPoint.setX(x);
    }

    public double getY() {
        return initialPoint.getY();
    }

    public void setY(double y){
        this.initialPoint.setY(y);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
