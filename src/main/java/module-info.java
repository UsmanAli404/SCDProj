module org.example.scdproj {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.scdproj to javafx.fxml;
    exports org.example.scdproj;
}