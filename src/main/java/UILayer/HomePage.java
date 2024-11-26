package UILayer;
import BusinessLayer.*;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class HomePage {
    private TaskService taskService;

    public HomePage(TaskService taskService) {
        this.taskService = taskService;
    }

    public VBox getView() {
        VBox root = new VBox(10);
        Button goToTasksButton = new Button("View Tasks");

        goToTasksButton.setOnAction(event -> MultiPageApp.navigateToTaskPage());

        root.getChildren().add(goToTasksButton);
        return root;
    }
}
