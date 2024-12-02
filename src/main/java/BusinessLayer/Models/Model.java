package BusinessLayer.Models;

import java.util.ArrayList;

//class diagram and usecase diagram will inherit from the model
public interface Model {
    public ArrayList<Component> components = new ArrayList<>();
    public void addComponent(Component c);
    public boolean removeComponent(Component c);
    public Component findComponent(Component c);
    public void draw();
}
