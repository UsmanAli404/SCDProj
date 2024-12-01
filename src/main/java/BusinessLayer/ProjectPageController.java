package BusinessLayer;

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

public class ProjectPageController {
    public String lastSelectedDiagramType;
    public TreeItem<String> lastSelectedTreeItem;
    public String projectPath;
    @FXML
    public Button add_model_btn;
    @FXML
    public Label project_name_label;
    @FXML
    public TreeView<String> model_explorer;
    @FXML
    public VBox model_VBox;
    //will hold a list of diagrams
    ArrayList<String> models;

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UILayer/addNewModelPage.fxml"));
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
                        models.add(modelName);
                    } else{
                        System.out.println("Target branch not found in model explorer!");
                    }
                }
            });

            // Show the dialog
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void remove_model_func(){
        //get the selected item
        //check if it is a valid item
        if(lastSelectedTreeItem.getParent()==null ||
                Objects.equals(lastSelectedTreeItem.getValue(), "Class Diagrams") ||
                Objects.equals(lastSelectedTreeItem.getValue(), "Usecase Diagrams")){
            return;
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

        System.out.println(selectedItem.getValue());

        if(Objects.equals(selectedItem.getParent().getValue(), "Class Diagrams")){
            //System.out.println("I am a class diagram!");
            changeVBox("Class Diagram", selectedItem.getValue());
        } else if(Objects.equals(selectedItem.getParent().getValue(), "Usecase Diagrams")){
            //System.out.println("I am a usecase diagram!");
            changeVBox("Usecase Diagram", selectedItem.getValue());
        } else if(selectedItem.getValue()=="Class Diagrams"){
            lastSelectedDiagramType="Class Diagram";
        } else if(selectedItem.getValue()=="Usecase Diagrams"){
            lastSelectedDiagramType="Usecase Diagram";
        }
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
            }
        } catch (IOException e){
            System.out.println("Error replacing contents of VBox!");
        }
    }
}
