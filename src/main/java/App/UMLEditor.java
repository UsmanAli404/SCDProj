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

public class UMLEditor extends Application {

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        UISetUp(stage);
    }

    public void UISetUp(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/UILayer/Pages/landingPage.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
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
        if(exit_alert.showAndWait().get()== ButtonType.OK){
            stage.close();
        } else {
            event.consume();
        }
    }
}
