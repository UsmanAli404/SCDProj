package BusinessLayer.Models;

import java.util.ArrayList;
import java.util.Objects;

public class Project {
    private String projectName;
    private String projectPath;
    private ArrayList<Model> models;

    public Project(){
        models = new ArrayList<>();
        projectName="";
        projectPath="";
    }

    public Project(String projectName, String projectPath){
        this.projectName=projectName;
        this.projectPath=projectPath;
        models = new ArrayList<>();
    }

    public ArrayList<Model> getModels() {
        return models;
    }

    public void setModels(ArrayList<Model> models) {
        this.models = models;
    }

    public boolean addModel(Model model){
        if(findModelByName(model.getModelName())==-1){//model doesn't exist
            models.add(model);
            return true;
        }
        return false;//model with the same name already exists
    }

    public boolean removeModel(String modelName){
        int index = findModelByName(modelName);
        if(index!=-1){//model exists
            models.remove(index);
            return true;
        }
        return false;
    }

    int findModelByName(String modelName){
        for(int i=0; i<models.size(); i++){
            if(Objects.equals(models.get(i).getModelName(), modelName)){
                return i;
            }
        }
        return -1;//not found
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }
}
