package BusinessLayer.Models.Diagrams;

import BusinessLayer.Models.Component;
import BusinessLayer.Models.Components.ClassDiagramComponents.*;
import BusinessLayer.Models.Components.ClassDiagramComponents.Class;
import BusinessLayer.Models.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
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
                if (component instanceof Class || component instanceof Interface) {
                    removeAssociations(component, removedComponents);
                }

                components.remove(index);
                //shift back the component counter of each component starting from index
                //starting from index as component at index is deleted and all the next
                //components have been shifted one step back
//                for (int i = index; i < components.size(); i++) {
//                    components.get(i).setId(i + 1);
//                }
//                upcomingComponentID = components.size();

                //check for associations
                return true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to remove component with ID: " + c.getId(), e);
            throw new RuntimeException(e);
        }
        return false;
    }

    public ArrayList<String> removeComponentByID(int id){
        ArrayList<String> removedComponents = new ArrayList<>();
        int index = findComponentByID(id);
        if(index!=-1){//component found
            Component component = components.get(index);
            removedComponents.add(component.getName()+" ("+component.getId()+")");
            System.out.println("removing component with id: "+component.getId());
            //first delete all associations
            if(component instanceof Class || component instanceof Interface){
                removeAssociations(component, removedComponents);
                removeDashedLines(component, removedComponents);
            } else if(component instanceof TextBox){
                removeDashedLines(component, removedComponents);
            }

            components.remove(index);
            System.out.println("component removed with id: "+id);

            //shift back the component counter of each component starting from index
            //starting from index as component at index is deleted and all the next
            //components have been shifted one step back
//            for(int i=index; i<components.size(); i++){
//                components.get(i).setId(i+1);
//            }
            //upcomingComponentID = components.size();
            //also need to update the tree and ui
        }
        return removedComponents;
    }

    public String getComponentNameForGivenId(int id){
        for(int i=0; i<components.size(); i++){
            if(components.get(i).getId()==id){
                return components.get(i).getName();
            }
        }
        return "";
    }

    public void printArr(){
        System.out.println("Components:");
        for(Component component : components){
            System.out.println("Component id: "+component.getId());
        }
        System.out.println("-----------------------------------------------");
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

    public void removeDashedLines(Component textBox, ArrayList<String> removedComponents){
        ArrayList<DashedLine> remove_indexes = new ArrayList<>();
        for(int i=0; i<components.size(); i++){
            Component c = components.get(i);
            if(c instanceof DashedLine dashedLine){
                if(dashedLine.getStartClass()==textBox || dashedLine.getEndClass()==textBox){
                    System.out.println("removing dashed line with id: "+c.getId());
                    remove_indexes.add((DashedLine) c);
                    removedComponents.add(dashedLine.getName()+" ("+dashedLine.getId()+")");
                }
            }
        }

        for(int i=0; i<remove_indexes.size(); i++){
            components.remove(remove_indexes.get(i));
        }
        System.out.println("dashed line removed!");
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
