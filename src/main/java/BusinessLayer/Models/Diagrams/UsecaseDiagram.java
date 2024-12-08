package BusinessLayer.Models.Diagrams;

import BusinessLayer.Models.Component;
import BusinessLayer.Models.Components.ClassDiagramComponents.Association;
import BusinessLayer.Models.Components.ClassDiagramComponents.Class;
import BusinessLayer.Models.Components.ClassDiagramComponents.Interface;
import BusinessLayer.Models.Components.UseCaseDiagramComponents.UseCase;
import BusinessLayer.Models.Components.UseCaseDiagramComponents.UseCaseActor;
import BusinessLayer.Models.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsecaseDiagram implements Model, Serializable {

    private static final Logger LOGGER = Logger.getLogger(UsecaseDiagram.class.getName());

    private String modelName = "";//unique for a project
    private ModelType modelType = null;//
    private ArrayList<Component> components = new ArrayList<>();
    private int upcomingComponentID = 1;

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
        ArrayList<String> removedComponents = new ArrayList<>();
        if (c == null) {
            LOGGER.warning("Cannot remove component: Provided component is null.");
            return false;
        }

        try {
            int index = findComponentByID(c.getId());
            if (index != -1) {//component found
                Component component = components.get(index);
                removedComponents.add(component.getName()+" ("+component.getId()+")");
                System.out.println("removing component with id: "+component.getId());
                //first delete all associations
                if (component instanceof UseCaseActor || component instanceof UseCase) {
                    removeAssociations(component, removedComponents);
                }

                components.remove(index);

                return true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to remove component with ID: " + c.getId(), e);
            throw new RuntimeException(e);
        }
        return false;
    }

    public ArrayList<String> removeComponentByID(int id){
        System.out.println("removeComponentByID(" + id + ")");
        ArrayList<String> removedComponents = new ArrayList<>();
        int index = findComponentByID(id);
        if(index!=-1){//component found
            Component component = components.get(index);
            removedComponents.add(component.getName()+" ("+component.getId()+")");
            System.out.println("removing component with id: "+component.getId());
            //first delete all associations
            if(component instanceof Class || component instanceof Interface){
                removeAssociations(component, removedComponents);
            }

            components.remove(index);
            System.out.println("component removed with id: "+id);

        }
        return removedComponents;
    }
    @Override
    public int findComponentByID(int id) {
        for (int i = 0; i < components.size(); i++) {
            if (Objects.equals(components.get(i).getId(), id)) {
                LOGGER.info("Component found with ID: " + id + " at index: " + i);
                return i;
            }
        }
        LOGGER.warning("No component found with ID: " + id);
        return -1;
    }
    public void removeAssociations(Component myClass, ArrayList<String> removedComponents){
        ArrayList<Association> remove_indexes = new ArrayList<>();
        for(int i=0; i<components.size(); i++){
            Component c = components.get(i);
            if(c instanceof Association association){
                if(association.getStartClass()==myClass || association.getEndClass()==myClass){
                    System.out.println("removing association with id: "+c.getId());
                    remove_indexes.add((Association) c);
                    removedComponents.add(association.getName()+" ("+association.getId()+")");
                }
            }
        }

        for(int i=0; i<remove_indexes.size(); i++){
            components.remove(remove_indexes.get(i));
        }
        System.out.println("association removed!");
    }

    public void printArr(){
        System.out.println("Components:");
        for(Component component : components){
            System.out.println("Component id: "+component.getId());
        }
        System.out.println("-----------------------------------------------");
    }

    @Override
    public String getModelName() {
        return modelName;
    }

    @Override
    public void setModelName(String modelName) {
        this.modelName=modelName;
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
