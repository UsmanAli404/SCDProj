package BusinessLayer.PageControllers;

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
import java.util.logging.Level;
import java.util.logging.Logger;

public class LandingPageController {
    private static final Logger LOGGER = Logger.getLogger(LandingPageController.class.getName());
    @FXML
    public void new_project_func(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/UILayer/Pages/newProjectPage.fxml"));
        Parent root;
        try {
            LOGGER.info("Attempting to load new project page...");
            root = fxmlLoader.load();
            LOGGER.info("Successfully loaded new project page.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load newProjectPage.fxml", e);
            throw new RuntimeException(e);
        }

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
        if (exit_alert.showAndWait().get() == ButtonType.OK) {
            stage.close();
            LOGGER.info("Application exited successfully.");
        } else {
            LOGGER.info("Exit canceled by user.");
        }
    }
}
