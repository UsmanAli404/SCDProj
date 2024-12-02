package BusinessLayer.VBoxControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;

//
public class ClassDiagramVBoxController {

    //for individual track of each and every button
    public ToggleButton currentlySelectedToggleBtn;
    @FXML
    public Label model_name;
    @FXML
    public ToggleButton class_toggle_btn;
    @FXML
    public ToggleButton interface_toggle_btn;
    @FXML
    public ToggleButton association_toggle_btn;
    @FXML
    public ToggleButton composition_toggle_btn;
    @FXML
    public ToggleButton aggregation_toggle_btn;
    @FXML
    public ToggleButton text_toggle_btn;
    @FXML
    public ToggleButton textbox_toggle_btn;
    @FXML
    public Button generateCode_btn;

    public void setModelName(String modelName){
        model_name.setText("Class Diagram: "+modelName);
    }


    public void generateCode_func(ActionEvent event) {
    }

    public void textbox_func(ActionEvent event) {
    }

    public void text_func(ActionEvent event) {
    }

    public void aggregation_func(ActionEvent event) {
    }

    public void composition_func(ActionEvent event) {
    }

    public void association_func(ActionEvent event) {
    }

    public void interface_func(ActionEvent event) {
    }

    public void class_func(ActionEvent event) {
    }
}
