package BusinessLayer.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A generic interface for UML diagram models.
 *
 * <p>
 * This interface serves as a base for both `ClassDiagram` and `UsecaseDiagram` classes.
 * It defines common functionality and properties that these diagrams share, such as managing
 * components, setting and retrieving model information, and keeping track of component IDs.
 * </p>
 */
public interface Model {

    /**
     * Represents the type of UML model.
     *
     * <p>
     * Possible values:
     * <ul>
     *     <li>ClassDiagram - Represents a UML class diagram.</li>
     *     <li>UsecaseDiagram - Represents a UML use case diagram.</li>
     * </ul>
     * </p>
     */
    enum ModelType {
        ClassDiagram, UsecaseDiagram
    }

    /**
     * Adds a component to the model.
     *
     * @param c The component to add.
     */
    void addComponent(Component c);

    /**
     * Removes a component from the model.
     *
     * @param c The component to remove.
     * @return True if the component was removed, false if it was not found.
     */
    boolean removeComponent(Component c);

    /**
     * Finds a component in the model by its unique ID.
     *
     * @param id The ID of the component to find.
     * @return The index of the component if found, or -1 if not found.
     */
    int findComponentByID(int id);

    /**
     * Retrieves the name of the model.
     *
     * @return The name of the model as a String.
     */
    String getModelName();

    /**
     * Sets the name of the model.
     *
     * @param modelName The new name for the model.
     */
    void setModelName(String modelName);

    /**
     * Retrieves the type of the model.
     *
     * @return The model type as a `ModelType` enum.
     */
    ModelType getModelType();

    /**
     * Sets the type of the model.
     *
     * @param modelType The new type for the model.
     */
    void setModelType(ModelType modelType);

    /**
     * Retrieves the list of components in the model.
     *
     * @return An ArrayList of `Component` objects.
     */
    ArrayList<Component> getComponents();

    /**
     * Sets the list of components in the model.
     *
     * @param components The new list of components.
     */
    void setComponents(ArrayList<Component> components);

    /**
     * Retrieves the upcoming unique component ID for the model.
     *
     * @return The next available component ID as an integer.
     */
    int getUpcomingComponentID();
}
