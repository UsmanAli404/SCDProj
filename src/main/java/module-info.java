module org.example.scdproj {
    requires javafx.controls;
    requires javafx.fxml;
    exports BusinessLayer to javafx.fxml;
    opens BusinessLayer to javafx.fxml;
    exports App;
    exports BusinessLayer.VBoxControllers to javafx.fxml;
    opens BusinessLayer.VBoxControllers to javafx.fxml;
}