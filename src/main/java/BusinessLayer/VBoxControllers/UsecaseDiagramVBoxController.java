package BusinessLayer.VBoxControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;

public class UsecaseDiagramVBoxController {
    @FXML
    public Label model_name;
    public ToggleButton usecase_toggle_btn;
    public ToggleButton actor_toggle_btn;
    public ToggleButton association_toggle_btn;
    public ToggleButton text_toggle_btn;
    public ToggleButton textbox_toggle_btn;

    public void setModelName(String modelName){
        model_name.setText("Usecase Diagram: "+modelName);
    }

    public void textbox_func(ActionEvent event) {
    }

    public void text_func(ActionEvent event) {
    }

    public void association_func(ActionEvent event) {
    }

    public void actor_func(ActionEvent event) {
    }

    public void usecase_func(ActionEvent event) {
    }
}
