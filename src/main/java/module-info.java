module com.tetkole.tetkole {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.datatransfer;
    requires java.desktop;


    opens com.tetkole.tetkole to javafx.fxml;
    exports com.tetkole.tetkole;
    exports com.tetkole.tetkole.controllers;
    opens com.tetkole.tetkole.controllers to javafx.fxml;
}