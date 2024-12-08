package BusinessLayer.PageControllers;

import BusinessLayer.Models.Diagrams.ClassDiagram;
import BusinessLayer.Models.Diagrams.UsecaseDiagram;
import BusinessLayer.Models.Model;
import BusinessLayer.Models.Project;
import BusinessLayer.OtherControllers.ClassDiagramController;
import BusinessLayer.OtherControllers.UsecaseDiagramController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.stage.DirectoryChooser;

import javax.imageio.ImageIO;
import java.io.File;

import javafx.scene.control.TextInputDialog;
import java.util.Optional;

public class ProjectPageController {

    private static final Logger LOGGER = Logger.getLogger(ProjectPageController.class.getName());

    public Project myProject;
    public String lastSelectedDiagramType="";//to preset the initial value of model type in AddNewMoodel dialog window
    public TreeItem<String> lastSelectedTreeItem;//keeps track of last selected item

    public ClassDiagram currentlySelectedClassDiagram = null;
    public ClassDiagramController currentlySelectedClassDiagramController = null;
    public UsecaseDiagram currentlySelectedUsecaseDiagram = null;
    public UsecaseDiagramController currentlySelectedUsecaseDiagramController = null;

    @FXML
    public Pane drawingPane;
    @FXML
    public Label project_name_label;
    @FXML
    public TreeView<String> model_explorer;
    @FXML
    public VBox model_VBox;
    @FXML
    public TextField selectedComponentTextField;

    public void initializeProject(String projectName, String projectPath){
        myProject = new Project(projectName, projectPath);
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
                        if(Objects.equals(modelType, "Class Diagram")){
                            BusinessLayer.Models.Diagrams.ClassDiagram classDiagram = new ClassDiagram();
                            classDiagram.setModelName(modelName);
                            myProject.addModel(classDiagram);
                        } else if(Objects.equals(modelType, "Usecase Diagram")){
                            BusinessLayer.Models.Diagrams.UsecaseDiagram usecaseDiagram = new UsecaseDiagram();
                            usecaseDiagram.setModelName(modelName);
                            myProject.addModel(usecaseDiagram);
                        }
                    } else{
                        System.out.println("Target branch not found in model explorer!");
                    }
                }
            });

            // Show the dialog
            dialog.showAndWait();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load addNewModelPage.fxml", e);
        }
    }

    public boolean modelAlreadyExists(String newModelName){
        return myProject.findModelByName(newModelName)!=-1;
    }

    @FXML
    public void remove_model_component_func(){
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
            myProject.removeModel((lastSelectedTreeItem.getValue()));
            drawingPane.getChildren().clear();

            selectedComponentTextField.clear();
            lastSelectedTreeItem = null;

            //update the model vbox with default ui
            changeVBox("default",currentlySelectedClassDiagram);
        } else {
            //level 4
            //find the component in the currently selected c
            //lastSelectedTreeItem holds the component:name (id)
            int ID = getSelectedComponentId();
            if(ID!=-1){
                if(lastSelectedDiagramType=="Class Diagram"){
                    currentlySelectedClassDiagram.printArr();
                    ArrayList<String> removedComponents = currentlySelectedClassDiagram.removeComponentByID(ID);
                    currentlySelectedClassDiagram.printArr();
                    //update the model explorer
                    //may need to remove one or two components
                    for(int i=0; i< removedComponents.size(); i++){
                        String componentNameID = removedComponents.get(i);
                        TreeItem<String> treeItem = findTreeItemWithGivenName(componentNameID);
                        TreeItem<String> parent = treeItem.getParent();
                        parent.getChildren().remove(treeItem);
                        int compId = getComponentIdBasedOnName(componentNameID);
                        currentlySelectedClassDiagramController.removeComponentWithGivenIDFromElementMap(compId);
                    }
                    currentlySelectedClassDiagramController.printMap();
                    //update ui
                    currentlySelectedClassDiagramController.redrawCanvas();
                } else if(lastSelectedDiagramType=="Usecase Diagram"){
                    System.out.println("attempting to delete component");
                    currentlySelectedUsecaseDiagram.printArr();
                    ArrayList<String> removedComponents = currentlySelectedUsecaseDiagram.removeComponentByID(ID);
                    currentlySelectedClassDiagram.printArr();
                    for(int i=0; i< removedComponents.size(); i++){
                        String componentNameID = removedComponents.get(i);
                        TreeItem<String> treeItem = findTreeItemWithGivenName(componentNameID);
                        TreeItem<String> parent = treeItem.getParent();
                        parent.getChildren().remove(treeItem);
                        int compId = getComponentIdBasedOnName(componentNameID);
                        currentlySelectedUsecaseDiagramController.removeComponentWithGivenIDFromElementMap(compId);
                    }
                    //currentlySelectedUsecaseDiagramController.printMap();
                    currentlySelectedUsecaseDiagramController.reDrawCanvas();
                }
            }
            selectedComponentTextField.clear();
        }
    }

    public int getSelectedComponentId(){
        return getComponentIdBasedOnName(selectedComponentTextField.getText());
    }

    //redraw tree
    public void redrawTree(){

    }

    @FXML
    public void selectItem(){
        TreeItem<String> selectedItem = model_explorer.getSelectionModel().getSelectedItem();

        if(selectedItem==null||selectedItem.getParent()==null){
            return;
        }

        lastSelectedTreeItem = selectedItem;

        //for when the selected item is at the model type layer
        if(Objects.equals(selectedItem.getValue(), "Class Diagrams")){
            lastSelectedDiagramType="Class Diagram";
            return;
        } else if(Objects.equals(selectedItem.getValue(), "Usecase Diagrams")){
            lastSelectedDiagramType="Usecase Diagram";
            return;
        }

        int level = getLevel(selectedItem, 0);

        if(level==3){
            int index = myProject.findModelByName(selectedItem.getValue());
            if(hasGivenParentUpIntheTree(lastSelectedTreeItem, "Class Diagrams")){
                lastSelectedDiagramType = "Class Diagram";
                currentlySelectedClassDiagram = (ClassDiagram) myProject.getModels().get(index);
                System.out.println(currentlySelectedClassDiagram.getModelName());
                lastSelectedDiagramType="Class Diagram";
                drawingPane.getChildren().clear();

                changeVBox("Class Diagram", currentlySelectedClassDiagram);
            } else if(hasGivenParentUpIntheTree(lastSelectedTreeItem, "Usecase Diagrams")){
                lastSelectedDiagramType = "Usecase Diagram";
                currentlySelectedUsecaseDiagram = (UsecaseDiagram) myProject.getModels().get(index);
                System.out.println(currentlySelectedUsecaseDiagram.getModelName());
                lastSelectedDiagramType="Usecase Diagram";
                drawingPane.getChildren().clear();

                changeVBox("Usecase Diagram", currentlySelectedUsecaseDiagram);
            }
        } else if(level==4){
            //set the appropriate diagram type and the currently selected diagram
            //in case its a class diagram
            if(hasGivenParentUpIntheTree(lastSelectedTreeItem, "Class Diagrams")){
                lastSelectedDiagramType = "Class Diagram";
                String parentName = lastSelectedTreeItem.getParent().getValue();
                currentlySelectedClassDiagram = (ClassDiagram) myProject.getModels().get(myProject.findModelByName(parentName));
                setSelectedComponentTextFieldText(lastSelectedTreeItem.getValue());
                System.out.println("Currently selected Class diagram name: "+ currentlySelectedClassDiagram.getModelName());

                int index = myProject.findModelByName(selectedItem.getParent().getValue());
                currentlySelectedClassDiagram = (ClassDiagram) myProject.getModels().get(index);
                System.out.println(currentlySelectedClassDiagram.getModelName());
                lastSelectedDiagramType="Class Diagram";
                drawingPane.getChildren().clear();

                changeVBox("Class Diagram", currentlySelectedClassDiagram);
            } else if(hasGivenParentUpIntheTree(lastSelectedTreeItem, "Usecase Diagrams")){
                lastSelectedDiagramType = "Usecase Digaram";
                String parentName = lastSelectedTreeItem.getParent().getValue();
                currentlySelectedUsecaseDiagram = (UsecaseDiagram) myProject.getModels().get(myProject.findModelByName(parentName));
                setSelectedComponentTextFieldText(lastSelectedTreeItem.getValue());
                System.out.println("Currently selected Usecase diagram name: "+ currentlySelectedUsecaseDiagram.getModelName());

                int index = myProject.findModelByName(selectedItem.getParent().getValue());
                currentlySelectedUsecaseDiagram = (UsecaseDiagram) myProject.getModels().get(index);
                System.out.println(currentlySelectedUsecaseDiagram.getModelName());
                lastSelectedDiagramType="Usecase Diagram";
                drawingPane.getChildren().clear();

                changeVBox("Usecase Diagram", currentlySelectedUsecaseDiagram);
            }
            getComponentIdBasedOnName(lastSelectedTreeItem.getValue());
        }
    }

    public int getComponentIdBasedOnName(String name){
        String[] data = name.split(" ");
        String id = "";

        for(String element: data){
            if(element.startsWith("(") && element.endsWith(")")){
                id = element.substring(1, element.length() - 1);
                break;
            }
        }

        if(id.isEmpty()){
            LOGGER.warning("failed to extract id from component name!");
            return -1;
        }

        LOGGER.info("Component ID: "+id);
        int ID = Integer.parseInt(id);
        return ID;
    }

    public boolean hasGivenParentUpIntheTree(TreeItem<String> treeItem, String name){
        if(treeItem == null){
            return false;
        }

        if(treeItem.getValue()==name){
            return true;
        }

        return hasGivenParentUpIntheTree(treeItem.getParent(), name);
    }

    public int getLevel(TreeItem<String> currentItem, int level){
        if(currentItem==null){
            return level;
        }

        level = level+1;

        return getLevel(currentItem.getParent(), level);
    }
    
    public TreeItem<String> getModelTreeItemByName(String name){
        TreeItem<String> temp = findTreeItemWithGivenName(model_explorer.getRoot(), name);
        return temp;
    }

    public TreeItem<String> findTreeItemWithGivenName(String name){
        TreeItem<String> res = findTreeItemWithGivenName(model_explorer.getRoot(), name);
        return res;
    }

    public TreeItem<String> findTreeItemWithGivenName(TreeItem<String> treeItem, String name){
        if(treeItem==null){
            return null;
        }

        if(Objects.equals(treeItem.getValue(), name)){
            return treeItem;
        }

        for(TreeItem<String> child: treeItem.getChildren()){
            TreeItem<String> res = findTreeItemWithGivenName(child, name);
            if(res!=null){
                return res;
            }
        }

        return null;
    }

    public void changeVBox(String type, Model diagram) {
        try {
            if (Objects.equals(type, "Class Diagram")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UILayer/VBoxes/classDiagramVBox.fxml"));
                Parent newContent = loader.load();
                ClassDiagramController classDiagramController = loader.getController();
                classDiagramController.classDiagram = (ClassDiagram) diagram;
                classDiagramController.setModelName(diagram.getModelName());
                classDiagramController.setProjectPageController(this);
                classDiagramController.setDrawingPane(drawingPane);
                classDiagramController.canvasInitialize();
                classDiagramController.redrawCanvas();
                currentlySelectedClassDiagramController = classDiagramController;

                model_VBox.getChildren().clear();
                model_VBox.getChildren().add(newContent);
            } else if (Objects.equals(type, "Usecase Diagram")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UILayer/VBoxes/useCaseDiagramVBox.fxml"));
                Parent newContent = loader.load();
                UsecaseDiagramController usecaseDiagramController = loader.getController();
                usecaseDiagramController.usecaseDiagram = (UsecaseDiagram) diagram;
                usecaseDiagramController.setModelName(diagram.getModelName());
                usecaseDiagramController.setProjectPageController(this);
                usecaseDiagramController.setDrawingPane(drawingPane);
                usecaseDiagramController.canvasInitialize();
                usecaseDiagramController.reDrawCanvas();
                currentlySelectedUsecaseDiagramController = usecaseDiagramController;

                model_VBox.getChildren().clear();
                model_VBox.getChildren().add(newContent);
            } else if(Objects.equals(type, "default")){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UILayer/VBoxes/defaultModelVBox.fxml"));
                Parent newContent = loader.load();

                model_VBox.getChildren().clear();
                model_VBox.getChildren().add(newContent);
            }
        } catch (IOException e){
            LOGGER.log(Level.SEVERE, "Error replacing contents of VBox!", e);
        }
    }

    public void setSelectedComponentTextFieldText(String text){
        selectedComponentTextField.setText(text);
    }

    private void exportNodeAsPng(Pane node, String fileName) {
        if (node.getWidth() <= 0 || node.getHeight() <= 0) {
            LOGGER.warning("Pane has no content to export.");
            return;
        }

        WritableImage writableImage = new WritableImage((int) node.getWidth(), (int) node.getHeight());
        node.snapshot(null, writableImage);

        File file = new File(fileName);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
            LOGGER.info("Exported successfully to " + file.getAbsolutePath());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export Successful");
            alert.setContentText("Exported image to " + file.getAbsolutePath());
            alert.showAndWait();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Could not export image", e);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export Failed");
            alert.setContentText("Failed to export the image. Please try again.");
            alert.showAndWait();
        }
    }


    public void loadFunc(ActionEvent event) {

    }

    public void saveFunc(ActionEvent event) {
        //get file name
        TextInputDialog fileNameDialog = new TextInputDialog(myProject.getProjectName()); // Default filename
        fileNameDialog.setTitle("Enter Project Name");
        fileNameDialog.setHeaderText("Enter a name for the Project file:");
        fileNameDialog.setContentText("Project name: ");

        // Get the file name from the user
        Optional<String> result = fileNameDialog.showAndWait();
        if (result.isPresent()) {
            String fileName = result.get().trim();
            if (!fileName.isEmpty()) {
                // Ensure the file has a .png extension
                if (!fileName.endsWith(".ser")) {
                    fileName += ".ser";
                }

                // Serialization
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
                    oos.writeObject(myProject);
                    System.out.println("myProject object serialized successfully!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void exportFunc(ActionEvent event) {
        //exportNodeAsPng(drawingPane, "output.png");

//        DirectoryChooser directoryChooser = new DirectoryChooser();
//        directoryChooser.setTitle("Select Directory to Save Image");
//
//        File directory = directoryChooser.showDialog(drawingPane.getScene().getWindow());
//        if (directory != null) {
//            File file = new File(directory, "output.png");
//            exportNodeAsPng(drawingPane, file.getAbsolutePath());
//        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory to Save Image");

        File directory = directoryChooser.showDialog(drawingPane.getScene().getWindow());
        if (directory != null) {
            // Ask the user for the file name
            TextInputDialog fileNameDialog = new TextInputDialog("output"); // Default filename
            fileNameDialog.setTitle("Enter File Name");
            fileNameDialog.setHeaderText("Enter a name for the image file:");
            fileNameDialog.setContentText("File name:");

            // Get the file name from the user
            Optional<String> result = fileNameDialog.showAndWait();
            if (result.isPresent()) {
                String fileName = result.get().trim();
                if (!fileName.isEmpty()) {
                    // Ensure the file has a .png extension
                    if (!fileName.endsWith(".png")) {
                        fileName += ".png";
                    }

                    // Create the file in the selected directory
                    File file = new File(directory, fileName);
                    exportNodeAsPng(drawingPane, file.getAbsolutePath());
                }
            }
        }
    }
}
