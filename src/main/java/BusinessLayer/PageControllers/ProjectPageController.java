package BusinessLayer.PageControllers;

import BusinessLayer.VBoxControllers.ClassDiagramVBoxController;
import BusinessLayer.VBoxControllers.UsecaseDiagramVBoxController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class ProjectPageController {
    public String lastSelectedDiagramType="";//to preset the initial value of model type in AddNewMoodel dialog window
    public TreeItem<String> lastSelectedTreeItem;//to remove the last selected item
    public String projectPath;//to save the project
    @FXML
    public Button add_model_btn;
    @FXML
    public Label project_name_label;//to save the project
    @FXML
    public TreeView<String> model_explorer;//to show the hierarchy of models and their components in the project
    @FXML
    public VBox model_VBox;//to show tools and annotations relevant to the model
    //will hold a list of diagrams
    ArrayList<String> models;//to store the list of models in the project, replace with actual model list

    public void setProjectPath(String path){
        projectPath = path;
    }

    public void setProjectName(String projectName){
        project_name_label.setText("Project Name: "+projectName);
    }

    public void initializeModelExplorer(){
        TreeItem<String> rootItem = new TreeItem<>("Models");

        TreeItem<String> classBranchItem = new TreeItem<>("Class Diagrams");
        TreeItem<String> useCaseBranchItem = new TreeItem<>("Usecase Diagrams");

        rootItem.getChildren().add(classBranchItem);
        rootItem.getChildren().add(useCaseBranchItem);

        model_explorer.setRoot(rootItem);
    }

    @FXML
    public void add_model_func() {
        try {
            // Load the FXML file for the dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UILayer/Pages/addNewModelPage.fxml"));
            DialogPane dialogPane = loader.load();

            // Set up the dialog
            javafx.scene.control.Dialog<ButtonType> dialog = new javafx.scene.control.Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Add New Model");

            // Access the controller
            AddNewModelPageController controller = loader.getController();
            controller.initializeModelComboBox();
            controller.setModelTypeComboBoxValue(lastSelectedDiagramType);

            // Get the OK button from the dialog
            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);

            // Add a custom handler for the OK button
            okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                String modelName = controller.getModelName();
                String modelType = controller.getModelType();

                if (modelName == null || modelName.trim().isEmpty()) {
                    // Show error message
                    Alert alertWindow = new Alert(Alert.AlertType.ERROR);
                    alertWindow.setTitle("Error!");
                    alertWindow.setContentText("You can't leave the model name field empty!");
                    alertWindow.showAndWait();

                    // Consume the event to prevent dialog from closing
                    event.consume();
                } else if(modelAlreadyExists(modelName)){
                    Alert alertWindow = new Alert(Alert.AlertType.ERROR);
                    alertWindow.setTitle("Error!");
                    alertWindow.setContentText("Model with the same name already exists!");
                    alertWindow.showAndWait();

                    // Consume the event to prevent dialog from closing
                    event.consume();
                } else {
                    // Valid input, proceed with adding the model
                    TreeItem<String> root = model_explorer.getRoot();
                    TreeItem<String> targetBranch = null;
                    for(TreeItem<String> item : root.getChildren()){
                        if(Objects.equals(item.getValue(), modelType+"s")){
                            targetBranch = item;
                            break;
                        }
                    }
                    if(targetBranch!=null){
                        TreeItem<String> newModelItem = new TreeItem<>(modelName);
                        targetBranch.getChildren().add(newModelItem);
                        //update the following line of code
                        models.add(modelName);
                    } else{
                        System.out.println("Target branch not found in model explorer!");
                    }
                }
            });

            // Show the dialog
            dialog.showAndWait();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public boolean modelAlreadyExists(String newModelName){
        return models.contains(newModelName);
    }

    @FXML
    public void remove_model_func(){
        //get the selected item
        //check if it is a valid item
        if (lastSelectedTreeItem == null) {
            return;
        }

        if(lastSelectedTreeItem.getParent()==null ||
                Objects.equals(lastSelectedTreeItem.getValue(), "Class Diagrams") ||
                Objects.equals(lastSelectedTreeItem.getValue(), "Usecase Diagrams")){
            Alert alertWindow = new Alert(Alert.AlertType.INFORMATION);
            alertWindow.setTitle("Info");
            alertWindow.setContentText("You can't delete headings from the model explorer!");
            alertWindow.showAndWait();
            return;
        }


        //check if it is a model or one of its components
        //in case it is a model
        if(Objects.equals(lastSelectedTreeItem.getParent().getValue(), "Class Diagrams") ||
                Objects.equals(lastSelectedTreeItem.getParent().getValue(), "Usecase Diagrams")){

            //confirmation message
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Warning");
            alert.setContentText("You are about to delete a model along with all of its components\n" +
                    "Do you wish to proceed?");
            alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get()==ButtonType.CANCEL){
                return;
            } else if(result.isEmpty()){
                return;
            }

            TreeItem<String> parent = lastSelectedTreeItem.getParent();
            parent.getChildren().remove(lastSelectedTreeItem);
            //remove the model along with its components from the models array
            //update the following line of code
            models.remove(lastSelectedTreeItem.getValue());

            lastSelectedTreeItem = null;

            //update the model vbox with default ui
            changeVBox("default","");
        }
    }



    public void initializeModels(){
        models = new ArrayList<>();
    }

    @FXML
    public void selectItem(){
        TreeItem<String> selectedItem = model_explorer.getSelectionModel().getSelectedItem();
        if(selectedItem==null||selectedItem.getParent()==null){
            return;
        }

        lastSelectedTreeItem = selectedItem;

        //System.out.println(selectedItem.getValue());

        //for when the selected item is at the model type layer
        if(Objects.equals(selectedItem.getValue(), "Class Diagrams")){
            lastSelectedDiagramType="Class Diagram";
            return;
        } else if(Objects.equals(selectedItem.getValue(), "Usecase Diagrams")){
            lastSelectedDiagramType="Usecase Diagram";
            return;
        }

        //only for when the selected item is 1 or more layers below the model type layer
        if(hasGivenParentUpInTheTree(lastSelectedTreeItem, "Class Diagrams")){
            changeVBox("Class Diagram", selectedItem.getValue());
            lastSelectedDiagramType="Class Diagram";
        } else if(hasGivenParentUpInTheTree(lastSelectedTreeItem, "Usecase Diagrams")){
            changeVBox("Usecase Diagram", selectedItem.getValue());
            lastSelectedDiagramType="Usecase Diagram";
        }
    }

    public boolean  hasGivenParentUpInTheTree(TreeItem<String> currentItem, String parentVal) {
        if (currentItem == null) {
            return false;
        }

        TreeItem<String> parentItem = currentItem.getParent();
        if (parentItem != null && parentVal.equals(parentItem.getValue())) {
            return true;
        }

        return hasGivenParentUpInTheTree(parentItem, parentVal);
    }

    public void changeVBox(String type, String modelName) {
        try {
            if (Objects.equals(type, "Class Diagram")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UILayer/VBoxes/classDiagramVBox.fxml"));
                Parent newContent = loader.load();
                ClassDiagramVBoxController classDiagramVBoxController = loader.getController();
                classDiagramVBoxController.setModelName(modelName);

                model_VBox.getChildren().clear();
                model_VBox.getChildren().add(newContent);
            } else if (Objects.equals(type, "Usecase Diagram")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UILayer/VBoxes/useCaseDiagramVBox.fxml"));
                Parent newContent = loader.load();
                UsecaseDiagramVBoxController usecaseDiagramVBoxController = loader.getController();
                usecaseDiagramVBoxController.setModelName(modelName);

                model_VBox.getChildren().clear();
                model_VBox.getChildren().add(newContent);
            } else if(Objects.equals(type, "default")){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UILayer/VBoxes/defaultModelVBox.fxml"));
                Parent newContent = loader.load();

                model_VBox.getChildren().clear();
                model_VBox.getChildren().add(newContent);
            }
        } catch (IOException e){
            System.out.println("Error replacing contents of VBox!");
        }
    }
}
