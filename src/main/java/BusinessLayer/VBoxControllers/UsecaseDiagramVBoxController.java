package BusinessLayer.VBoxControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class UsecaseDiagramVBoxController {
    @FXML
    public Label model_name;

    public void setModelName(String modelName){
        model_name.setText("Usecase Diagram: "+modelName);
    }
}
