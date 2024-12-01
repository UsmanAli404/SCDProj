package BusinessLayer;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class NewProjectPageController {
    @FXML
    TextField project_name_field;
    @FXML
    TextField path_field;
    @FXML
    Button back_btn;

    @FXML
    public void pick_file_func(){
        //open file picker
        //fill path in the file path
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open the folder where you want to save this project");
        File selectedDirectory = directoryChooser.showDialog(null);
        if(selectedDirectory!=null){
            path_field.setText(selectedDirectory.getPath());
        }
    }

    @FXML
    public void make_project_func(ActionEvent event) throws IOException {
        if(validation()){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/UILayer/ProjectPage.fxml"));
            Parent root = fxmlLoader.load();

            ProjectPageController projectPageController = fxmlLoader.getController();
            projectPageController.setProjectName(project_name_field.getText());
            projectPageController.setProjectPath(path_field.getText());
            projectPageController.initializeModelExplorer();
            projectPageController.initializeModels();

            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("One or more text fields are empty!");
            alert.showAndWait();
        }
    }

    public boolean validation(){
        String path = path_field.getText();
        String name = project_name_field.getText();

        if(path.isEmpty()||name.isEmpty()){
            return false;
        }

        return true;
    }

    @FXML
    public void back_to_landing_page_func(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/UILayer/landingPage.fxml"));
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }
}
