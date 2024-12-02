package BusinessLayer.Models;

import java.util.ArrayList;

//class diagram and usecase diagram will inherit from the model
public interface Model {
    enum ModelType{
        ClassDiagram, UsecaseDiagram
    }

    public void addComponent(Component c);
    public boolean removeComponent(Component c);
    public int findComponentByID(int id);
    public String getModelName();
    public void setModelName(String modelName);
    public ModelType getModelType();
    public void setModelType(ModelType modelType);
    public ArrayList<Component> getComponents();
    public void setComponents(ArrayList<Component> components);
    public int getUpcomingComponentID();
    public void draw();
}
