package BusinessLayer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;

public class LandingPageController {
    @FXML
    public void new_project_func(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/UILayer/newProjectPage.fxml"));
        Parent root = fxmlLoader.load();

        NewProjectPageController newProjectPageController = fxmlLoader.getController();
        newProjectPageController.setItemsList();

        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
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
