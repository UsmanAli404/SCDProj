package App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class UMLEditor extends Application {

    private static final Logger LOGGER = Logger.getLogger(UMLEditor.class.getName());

    public static void main(String[] args){
        try {
            LogManager.getLogManager().readConfiguration(UMLEditor.class.getResourceAsStream("/logging.properties"));
            LOGGER.info("Logging system initialized!");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not set up logger configuration.", e);
        }
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        UISetUp(stage);
    }

    public void UISetUp(Stage stage){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/UILayer/Pages/landingPage.fxml"));
        try {
            Parent root = fxmlLoader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load landingPage.fxml.", e);
            throw new RuntimeException(e);
        }
        stage.setOnCloseRequest(windowEvent -> {
            w_exit(stage, windowEvent);
        });

        stage.setTitle("Best UML Editor!");
        Image img = new Image("me.png");
        stage.getIcons().add(img);
        stage.show();
    }

    public void w_exit(Stage stage, WindowEvent event){
        Alert exit_alert = new Alert(Alert.AlertType.CONFIRMATION);
        exit_alert.setTitle("Exit Confirmation");
        exit_alert.setContentText("Do you want to exit?");
        try {
            if (exit_alert.showAndWait().get() == ButtonType.OK) {
                stage.close();
            } else {
                event.consume();
            }
        } catch (NoSuchElementException e){
            LOGGER.log(Level.WARNING, "Could not find element", e);
        }
    }
}
