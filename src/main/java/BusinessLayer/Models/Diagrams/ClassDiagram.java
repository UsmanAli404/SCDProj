package BusinessLayer.Models.Diagrams;

import BusinessLayer.Models.Component;
import BusinessLayer.Models.Model;

import java.util.ArrayList;
import java.util.Objects;

public class ClassDiagram implements Model {
    private String modelName = "";//unique for a project
    private ModelType modelType = null;//
    private ArrayList<Component> components = new ArrayList<>();
    private int upcomingComponentID = 1;//holds the id of the coming component

    @Override
    public void addComponent(Component c) {
        //check the counter first before adding
        if(findComponentByID(c.getId())==-1){//if no such component is found
            components.add(c);
            upcomingComponentID++;
        }
    }

    @Override
    public boolean removeComponent(Component c) {
        int index = findComponentByID(c.getId());
        if(index!=-1){//component found
            components.remove(index);
            //shift back the component counter of each component starting from index
            //starting from index as component at index is deleted and all the next
            //components have been shifted one step back
            for(int i=index; i<components.size(); i++){
                components.get(i).setId(i+1);
            }
            upcomingComponentID = components.size();
            return true;
        }
        return false;
    }

    @Override
    public int findComponentByID(int id) {
        for(int i=0; i<components.size(); i++){
            if(Objects.equals(components.get(i).getId(), id)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public String getModelName() {
        return modelName;
    }

    @Override
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public ModelType getModelType() {
        return modelType;
    }

    @Override
    public void setModelType(ModelType modelType) {
        this.modelType = modelType;
    }

    @Override
    public ArrayList<Component> getComponents() {
        return components;
    }

    @Override
    public void setComponents(ArrayList<Component> components) {
        this.components = components;
    }

    @Override
    public int getUpcomingComponentID() {
        return upcomingComponentID;
    }

    @Override
    public void draw() {
        for(int i=0; i<components.size(); i++){
            components.get(i).draw();
        }
    }
}
