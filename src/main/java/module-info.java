module org.example.scdproj {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;
    requires batik.all;
    requires java.logging;


    // Export necessary packages to be used by other modules
    exports BusinessLayer.OtherControllers to javafx.fxml;
    opens BusinessLayer.OtherControllers to javafx.fxml;
    exports BusinessLayer.Models;
    exports BusinessLayer.Models.Components.UseCaseDiagramComponents;
    exports BusinessLayer.Models.Components.ClassDiagramComponents;
    // Open the PageControllers package to allow testing with JUnit
    opens BusinessLayer.PageControllers; // This is the fix
    exports BusinessLayer.PageControllers; // This may also be necessary
    // Exported to all modules

    exports App;
}
