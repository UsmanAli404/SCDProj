package BusinessLayer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class LandingPageController {
    @FXML
    public void w_exit(ActionEvent event){
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();

        Alert exit_alert = new Alert(Alert.AlertType.CONFIRMATION);
        exit_alert.setTitle("Exit Confirmation");
        exit_alert.setContentText("Do you want to exit?");
        if(exit_alert.showAndWait().get()== ButtonType.OK){
            stage.close();
        }
    }
}
