package BusinessLayer;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class NewProjectPageController {
    @FXML
    TextField project_name_field;
    @FXML
    ChoiceBox<String> diagram_choiceBox;
    @FXML
    TextField path_field;

    public void setItemsList(){
        diagram_choiceBox.setItems(FXCollections.observableArrayList("Class Diagram", "Usecase Diagram"));
        diagram_choiceBox.setValue("Class Diagram");
    }

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
    public void make_project_func(){
        if(validation()){

        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
}
