module com.tetkole.tetkole {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.datatransfer;
    requires java.desktop;
    requires jave.core;
    requires org.json;
    requires java.net.http;
    requires org.apache.httpcomponents.httpmime;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;


    opens com.tetkole.tetkole to javafx.fxml;
    exports com.tetkole.tetkole;
    exports com.tetkole.tetkole.controllers;
    exports com.tetkole.tetkole.controllers.components;
    exports com.tetkole.tetkole.utils.wave;
    exports com.tetkole.tetkole.utils.annotations;
    exports com.tetkole.tetkole.utils.models;
    opens com.tetkole.tetkole.controllers to javafx.fxml;
}