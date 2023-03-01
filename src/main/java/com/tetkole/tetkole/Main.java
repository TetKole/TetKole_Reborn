package com.tetkole.tetkole;

import com.tetkole.tetkole.utils.AuthenticationManager;
import com.tetkole.tetkole.utils.FileManager;
import com.tetkole.tetkole.utils.HttpRequestManager;
import com.tetkole.tetkole.utils.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

        stage.setTitle("TetKole");

        // Instanciate Singletons
        SceneManager.setSceneManager(stage);
        FileManager.setFileManager();
        AuthenticationManager.setAuthenticationManager();
        HttpRequestManager.setHttpRequestManagerInstance("http://localhost:8000"); // TODO .env

        SceneManager.getSceneManager().changeScene("HomeScene.fxml", (int) (dimension.width * 0.8),  (int) (dimension.height * 0.8));

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}