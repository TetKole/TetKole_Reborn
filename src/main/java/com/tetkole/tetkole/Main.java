package com.tetkole.tetkole;

import com.tetkole.tetkole.utils.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.awt.Dimension;
import java.awt.Toolkit;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

        stage.setTitle("TètKole");
        SceneManager.setSceneManager(stage);
        SceneManager.getSceneManager().changeScene("MainMenuScene.fxml", (int) (dimension.width * 0.8),  (int) (dimension.height * 0.8));

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}