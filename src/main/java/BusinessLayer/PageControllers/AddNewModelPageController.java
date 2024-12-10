package BusinessLayer.PageControllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class AddNewModelPageController {
    @FXML
    public TextField model_name_field;
    @FXML
    ComboBox<String> model_type_comboBox;

    public void initializeModelComboBox(){
        model_type_comboBox.setItems(FXCollections.observableArrayList("Class Diagram", "Usecase Diagram"));
        setModelTypeComboBoxValue("Class Diagram");
    }

    public void setModelTypeComboBoxValue(String value){
        if(value.isEmpty()){
            value="Class Diagram";
        }
        model_type_comboBox.setValue(value);
    }

    public String getModelName(){
        return model_name_field.getText();
    }

    public String getModelType(){
        return model_type_comboBox.getValue();
    }
}
