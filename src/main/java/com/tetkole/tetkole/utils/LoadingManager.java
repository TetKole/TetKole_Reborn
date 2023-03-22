package com.tetkole.tetkole.utils;

import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class LoadingManager {

    private static LoadingManager loadingManagerInstance;

    private ProgressIndicator progressIndicator;

    private LoadingManager() {
        this.progressIndicator = new ProgressIndicator();
    }

    public static LoadingManager getLoadingManagerInstance() {
        return loadingManagerInstance;
    }

    public static void setLoadingManagerInstance() {
        LoadingManager.loadingManagerInstance = new LoadingManager();
    }

    public void displayLoading(StackPane stackPane) {
        Platform.runLater(() -> {
            Pane pane = new Pane();
            pane.setStyle("-fx-background-color: rgba(50, 50, 50, 0.5);");
            pane.setId("transparent-loading");
            stackPane.getChildren().add(pane);
            stackPane.getChildren().add(progressIndicator);
        });
    }

    public void hideLoading(StackPane stackPane) {
        Platform.runLater(() -> {
            // delete loading
            for (var element: stackPane.getChildren()) {
                if (element instanceof ProgressIndicator) {
                    stackPane.getChildren().remove(element);
                    break;
                }
            }

            // delete pane
            for (var element: stackPane.getChildren()) {
                if (element instanceof Pane) {
                    Pane pane = (Pane)element;
                    if (pane.getId() != null && pane.getId().equals("transparent-loading")) {
                        stackPane.getChildren().remove(element);
                        break;
                    }
                }
            }
        });
    }
}
