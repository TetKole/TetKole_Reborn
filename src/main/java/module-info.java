module com.tetkole.tetkole {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.datatransfer;
    requires java.desktop;
    requires jave.core;
    requires org.json;


    opens com.tetkole.tetkole to javafx.fxml;
    exports com.tetkole.tetkole;
    exports com.tetkole.tetkole.controllers;
    exports com.tetkole.tetkole.controllers.components;
    exports com.tetkole.tetkole.utils.wave;
    opens com.tetkole.tetkole.controllers to javafx.fxml;
}