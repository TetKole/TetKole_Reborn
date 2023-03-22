package com.tetkole.tetkole.utils;

import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;

public class LoadingManager {

    private static LoadingManager loadingManagerInstance;

    ProgressIndicator progressIndicator;

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
        Platform.runLater(() -> stackPane.getChildren().add(progressIndicator));
    }

    public void hideLoading(StackPane stackPane) {
        Platform.runLater(() -> {
            for (var element: stackPane.getChildren()) {
                if (element instanceof ProgressIndicator) {
                    stackPane.getChildren().remove(element);
                    break;
                }
            }
        });
    }
}
