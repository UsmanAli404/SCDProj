module org.example.scdproj {
    requires javafx.controls;
    requires javafx.fxml;
    exports BusinessLayer.VBoxControllers to javafx.fxml;
    opens BusinessLayer.VBoxControllers to javafx.fxml;
    exports BusinessLayer.PageControllers to javafx.fxml;
    opens BusinessLayer.PageControllers to javafx.fxml;
    exports App;
}