package com.tetkole.tetkole.components;

import com.tetkole.tetkole.utils.enums.ToastTypes;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.io.IOException;

public class ToastComponent extends AnchorPane {

    @FXML
    private HBox toastBox;
    @FXML
    private Label toastLabel;
    private final TranslateTransition translateTransition;
    private final SequentialTransition sequentialTransition;

    public ToastComponent() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "ToastComponent.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.setTranslateY(180);

        translateTransition = new TranslateTransition();
        translateTransition.setDuration(Duration.seconds(0.5));
        translateTransition.setNode(this);
        translateTransition.setToY(-10);
        translateTransition.setFromY(180);

        PauseTransition pauseTransition = new PauseTransition();
        pauseTransition.setDuration(Duration.seconds(1));


        sequentialTransition = new SequentialTransition(translateTransition, pauseTransition);
        sequentialTransition.setCycleCount(2);
        sequentialTransition.setAutoReverse(true);
    }

    public void setToast(String toastText, ToastTypes type) {
        // This line is to be able to call this method even in a thread
        // Solution seen here : https://stackoverflow.com/a/55258445
        Platform.runLater(() -> {

            toastBox.getMaxWidth();

            // Calculation to adapt the animation to the message height with text wrapping
            double fontSize = toastLabel.getFont().getSize();
            int nbOfLines = (int) (fontSize * toastText.length() / toastBox.getMaxWidth());
            translateTransition.setFromY(180 + nbOfLines * toastLabel.getFont().getSize());

            setText(toastText);

            switch (type) {
                case ERROR -> toastBox.setStyle("-fx-background-color: red");
                case SUCCESS -> toastBox.setStyle("-fx-background-color: green");
            }

            sequentialTransition.play();
        });




    }

    public String getText() {
        return toastLabel.getText();
    }

    public void setText(String value) {
        toastLabel.setText(value);
    }
}
