package UILayer;

import BusinessLayer.*;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class TaskPage {
    private TaskService taskService;

    public TaskPage(TaskService taskService) {
        this.taskService = taskService;
    }

    public VBox getView() {
        VBox root = new VBox(10);
        ListView<String> taskList = new ListView<>();
        taskList.getItems().setAll(taskService.getAllTasks());

        Button backButton = new Button("Back to Home");
        backButton.setOnAction(event -> MultiPageApp.navigateToHomePage());

        root.getChildren().addAll(taskList, backButton);
        return root;
    }
}
