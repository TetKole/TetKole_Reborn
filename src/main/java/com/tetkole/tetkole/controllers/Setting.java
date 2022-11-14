package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.SceneManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import java.net.URL;
import java.util.*;

public class Setting implements Initializable {

    @FXML
    private ComboBox<String> comboBox;

    private Locale frLocal = new Locale("fr", "FR");
    private Locale enLocal = new Locale("en", "EN");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // add items to ComboBox
        comboBox.getItems().add(resources.getString("French"));
        comboBox.getItems().add(resources.getString("English"));

        // set default
        if (resources.getLocale().equals(frLocal)) {
            comboBox.getSelectionModel().select(resources.getString("French"));
        }
        if (resources.getLocale().equals(enLocal)) {
            comboBox.getSelectionModel().select(resources.getString("English"));
        }

        // on value changed
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(resources.getString("French"))) {
                SceneManager.getSceneManager().changeLocale(frLocal);
            }
            if (newValue.equals(resources.getString("English"))) {
                SceneManager.getSceneManager().changeLocale(enLocal);
            }
        });
    }

    @FXML
    private void onReturn() {
        SceneManager.getSceneManager().changeScene("main_menu.fxml");
    }
}
