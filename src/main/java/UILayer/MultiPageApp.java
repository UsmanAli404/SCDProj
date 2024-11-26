package UILayer;

import BusinessLayer.*;
import DataAcessLayer.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MultiPageApp extends Application {
    private static Stage primaryStage;
    private static TaskService taskService = new TaskServiceImpl(new TaskRepositoryImpl());

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        navigateToHomePage();
        primaryStage.setTitle("Multi-Page JavaFX App");
        primaryStage.show();
    }

    public static void navigateToHomePage() {
        HomePage homePage = new HomePage(taskService);
        primaryStage.setScene(new Scene(homePage.getView(), 400, 300));
    }

    public static void navigateToTaskPage() {
        TaskPage taskPage = new TaskPage(taskService);
        primaryStage.setScene(new Scene(taskPage.getView(), 400, 300));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
