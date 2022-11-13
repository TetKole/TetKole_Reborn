package com.tetkole.tetkole.utils;

import com.tetkole.tetkole.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class SceneManager {

    private static SceneManager sceneManagerInstance;

    private final Stage stage;
    private Locale current_locale;
    private String currentResourceName;
    private Map<String, Object> sceneArguments;
    private final Map<String, Object> wantedArguments;

    private SceneManager(Stage stage) {
        this.stage = stage;
        this.sceneArguments = new HashMap<>();
        this.wantedArguments = new HashMap<>();
        current_locale = new Locale("fr", "FR");
    }

    /**
     * Method that loads a scene with ressourceName and replaces it in the
     * stage.
     * @param resourceName name of the resource file (something.fxml)
     * @param width width of the new scene
     * @param height height of the new scene
     */
    private void loadScene(String resourceName, double width, double height) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(resourceName));
        fxmlLoader.setResources(ResourceBundle.getBundle("languages", current_locale));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(Objects.requireNonNull(Main.class.getResource(resourceName)).openStream()), width, height);
            String css = Objects.requireNonNull(SceneManager.class.getResource("/style.css")).toExternalForm();
            scene.getStylesheets().add(css);

            stage.setScene(scene);
            currentResourceName = resourceName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setSceneManager(Stage stage) {
        sceneManagerInstance = new SceneManager(stage);
    }
    public static SceneManager getSceneManager() {
        return sceneManagerInstance;
    }

    public void addArgument(String key, Object sceneArgument) {
        wantedArguments.put(key, sceneArgument);
    }

    public Object getArgument(String key) {
        return sceneArguments.get(key);
    }


    public void changeScene(String resourceName, double width, double height)  {
        loadScene(resourceName, width, height);
        sceneArguments = wantedArguments;
        wantedArguments.clear();
    }

    public void changeScene(String resourceName) {
        changeScene(resourceName, stage.getScene().getWidth(), stage.getScene().getHeight());
    }

    public void changeLocale(Locale locale) {
        current_locale = locale;
        loadScene(currentResourceName, stage.getScene().getWidth(), stage.getScene().getHeight());
    }
}
