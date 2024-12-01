package BusinessLayer.VBoxControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ClassDiagramVBoxController {
    @FXML
    public Label model_name;

    public void setModelName(String modelName){
        model_name.setText("Class Diagram: "+modelName);
    }
}
