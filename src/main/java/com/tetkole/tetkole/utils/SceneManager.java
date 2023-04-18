package com.tetkole.tetkole.utils;

import com.tetkole.tetkole.Main;
import com.tetkole.tetkole.components.ToastComponent;
import com.tetkole.tetkole.utils.enums.ToastTypes;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class SceneManager {

    private static SceneManager sceneManagerInstance;

    private final Stage stage;
    private final StackPane root;
    private final Scene mainScene;
    private Locale currentLocale;
    private String currentResourceName;
    private Map<String, Object> sceneArguments;
    private final Map<String, Object> wantedArguments;
    private Stage currentModalStage;
    private String modalParameterValue;
    private String modalDescriptionValue;

    private final ToastComponent toastComponent;


    private SceneManager(Stage stage) {
        this.stage = stage;
        this.root = new StackPane();
        mainScene = new Scene(root, 1000, 1000);
        stage.setScene(mainScene);
        String css = Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm();
        mainScene.getStylesheets().setAll(css);
        this.sceneArguments = new HashMap<>();
        this.wantedArguments = new HashMap<>();
        currentLocale = new Locale("fr", "FR");
        toastComponent = new ToastComponent();
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
        fxmlLoader.setResources(ResourceBundle.getBundle("languages", currentLocale));
        root.getChildren().clear();
        try {
            stage.setHeight(height);
            stage.setWidth(width);

            Node resourceRoot = fxmlLoader.load();

            root.getChildren().add(resourceRoot);
            root.getChildren().add(toastComponent);

            toastComponent.setFocusTraversable(false);
            toastComponent.setPickOnBounds(false);

            currentResourceName = resourceName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.gc();
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
        sceneArguments.clear();
        sceneArguments = wantedArguments;
        wantedArguments.clear();
    }

    public void changeScene(String resourceName) {
        changeScene(resourceName, stage.getWidth(), stage.getHeight());
    }

    public void changeLocale(Locale locale) {
        currentLocale = locale;
        loadScene(currentResourceName, stage.getWidth(), stage.getHeight());
    }

    public String showNewModal(String resourceName, String modalParameter, String modalDescription) {
        this.modalParameterValue = modalParameter;
        this.modalDescriptionValue = modalDescription;
        try {
            final Stage dialog = new Stage();
            dialog.setResizable(false);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(stage);

            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(resourceName));
            fxmlLoader.setResources(ResourceBundle.getBundle("languages", currentLocale));

            Parent modalRoot = fxmlLoader.load(Objects.requireNonNull(Main.class.getResource(resourceName)).openStream());

            Scene dialogScene = new Scene(modalRoot, 300, 200);
            String css = Objects.requireNonNull(SceneManager.class.getResource("/style.css")).toExternalForm();
            dialogScene.getStylesheets().add(css);
            dialog.setScene(dialogScene);

            this.currentModalStage = dialog;
            dialog.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this.modalParameterValue;
    }

    public void closeModal(String modalReturnValue) {
        this.modalParameterValue = modalReturnValue;
        currentModalStage.close();
    }

    public void closeAlertModal() {
        currentModalStage.close();
    }

    public String getModalParameterValue() {
        return modalParameterValue;
    }

    public String getModalDescriptionValue() {
        return modalDescriptionValue;
    }

    public Locale getCurrentLocale() {
        return this.currentLocale;
    }

    public String getResourceString(String key) {
        return ResourceBundle.getBundle("languages", currentLocale).getString(key);
    }

    public Stage getStage() {
        return stage;
    }

    public Double getStageHeight() { return stage.getHeight(); }

    public void sendToast(String toastText, ToastTypes type) {
        this.toastComponent.setToast(toastText, type);
    }
}
