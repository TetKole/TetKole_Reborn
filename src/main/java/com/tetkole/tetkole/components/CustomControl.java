package com.tetkole.tetkole.components;

import java.io.IOException;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class CustomControl extends VBox {
    @FXML private TextField textField;

    @FXML private Button button;

    public CustomControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "custom_control.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public String getText() {
        return button.getText();
    }

    public void setText(String value) {
        button.setText(value);
    }

    @FXML
    protected void doSomething() {
        System.out.println("The button was clicked!");
    }
}