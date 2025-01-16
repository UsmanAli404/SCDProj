package BusinessLayer.Models.Diagrams;

import BusinessLayer.Models.Component;
import BusinessLayer.Models.Components.ClassDiagramComponents.*;
import BusinessLayer.Models.Components.ClassDiagramComponents.Classes.Class;
import BusinessLayer.Models.Components.ClassDiagramComponents.Classes.Interface;
import BusinessLayer.Models.Components.ClassDiagramComponents.Line.Line;
import BusinessLayer.Models.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

/**
 *
 * */
public class ClassDiagram implements Model, Serializable {

    private static final Logger LOGGER = Logger.getLogger(ClassDiagram.class.getName());

    private String modelName = "";//unique for a project
    private ModelType modelType = null;//
    private ArrayList<Component> components = new ArrayList<>();
    private int upcomingComponentID = 1;//holds the id of the coming component

    @Override
    public void addComponent(Component c) {
        //check the counter first before adding
        if(findComponentByID(c.getId())==-1){//if no such component is found
//            if(c instanceof Association ||
//                    c instanceof Aggregation ||
//                    c instanceof Inheritance ||
//                    c instanceof Composition ||
//                    c instanceof DashedLine){
//                components.addFirst(c);
//            } else {
//                components.add(c);
//            }

            components.add(c);

            upcomingComponentID++;
        }
    }

    @Override
    public boolean removeComponent(Component c) {
        return !removeComponentByID(c.getId()).isEmpty();
    }

    public ArrayList<String> removeComponentByID(int id){
        ArrayList<String> removedComponents = new ArrayList<>();
        int index = findComponentByID(id);
        if(index!=-1){//component found
            Component component = components.get(index);
            removedComponents.add(component.getName()+" ("+component.getId()+")");
            System.out.println("removing component with id: "+component.getId());
            //first delete all associations
            if(component instanceof Class || component instanceof Interface || component instanceof TextBox){
                removeAssociatedLines(component, removedComponents);
            }

            components.remove(index);
            System.out.println("component removed with id: "+id);
        }
        return removedComponents;
    }

    public void printArr(){
        System.out.println("Components:");
        for(Component component : components){
            System.out.println("Component id: "+component.getId());
        }
        System.out.println("-----------------------------------------------");
    }

    public void removeAssociatedLines(Component component, ArrayList<String> removedComponents){
        ArrayList<Line> remove_indexes = new ArrayList<>();
        for(int i=0; i<components.size(); i++){
            Component c = components.get(i);
            if(c instanceof Line line){
                if(line.getStartComp()==component || line.getEndComp()==component){
                    //System.out.println("removing association with id: "+c.getId());
                    remove_indexes.add((Line) c);
                    removedComponents.add(line.getName()+" ("+line.getId()+")");
                }
            }
        }

        for(int i=0; i<remove_indexes.size(); i++){
            components.remove(remove_indexes.get(i));
        }
        //System.out.println("line removed!");
    }

    @Override
    public int findComponentByID(int id) {
        for (int i = 0; i < components.size(); i++) {
            if (Objects.equals(components.get(i).getId(), id)) {
                LOGGER.info("Component found with ID: " + id + " at index: " + i);
                return i;
            }
        }
        LOGGER.info("No component found with ID: " + id);
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
}
