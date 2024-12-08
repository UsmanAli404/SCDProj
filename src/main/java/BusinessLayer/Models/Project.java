package BusinessLayer.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a project in the system.
 *
 * <p>
 * A project stores the project name, its directory path, and a list of models it contains.
 * </p>
 */
public class Project implements Serializable {

    /**
     * The name of the project.
     */
    private String projectName;

    /**
     * The directory path of the project.
     */
    private String projectPath;

    /**
     * The list of models associated with the project.
     */
    private ArrayList<Model> models;

    /**
     * Default constructor that initializes the project with an empty name, path, and model list.
     */
    public Project() {
        models = new ArrayList<>();
        projectName = "";
        projectPath = "";
    }

    /**
     * Constructor to initialize the project with a specific name and path.
     *
     * @param projectName The name of the project.
     * @param projectPath The directory path of the project.
     */
    public Project(String projectName, String projectPath) {
        this.projectName = projectName;
        this.projectPath = projectPath;
        this.models = new ArrayList<>();
    }

    /**
     * Retrieves the list of models in the project.
     *
     * @return An ArrayList of models.
     */
    public ArrayList<Model> getModels() {
        return models;
    }

    /**
     * Sets the list of models in the project.
     *
     * @param models The new list of models.
     */
    public void setModels(ArrayList<Model> models) {
        this.models = models;
    }

    /**
     * Adds a model to the project if a model with the same name does not already exist.
     *
     * @param model The model to add.
     * @return True if the model was added, false if a model with the same name already exists.
     */
    public boolean addModel(Model model) {
        if (findModelByName(model.getModelName()) == -1) { // Model doesn't exist
            models.add(model);
            return true;
        }
        return false; // Model with the same name already exists
    }

    /**
     * Removes a model from the project by its name.
     *
     * @param modelName The name of the model to remove.
     * @return True if the model was removed, false if no model with the given name was found.
     */
    public boolean removeModel(String modelName) {
        int index = findModelByName(modelName);
        if (index != -1) { // Model exists
            models.remove(index);
            return true;
        }
        return false; // Model not found
    }

    /**
     * Finds the index of a model by its name.
     *
     * @param modelName The name of the model to search for.
     * @return The index of the model if found, or -1 if no model with the given name exists.
     */
    public int findModelByName(String modelName) {
        for (int i = 0; i < models.size(); i++) {
            if (Objects.equals(models.get(i).getModelName(), modelName)) {
                return i;
            }
        }
        return -1; // Not found
    }

    /**
     * Retrieves the name of the project.
     *
     * @return The project name.
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Sets the name of the project.
     *
     * @param projectName The new project name.
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * Retrieves the directory path of the project.
     *
     * @return The project path.
     */
    public String getProjectPath() {
        return projectPath;
    }

    /**
     * Sets the directory path of the project.
     *
     * @param projectPath The new project path.
     */
    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }
}
