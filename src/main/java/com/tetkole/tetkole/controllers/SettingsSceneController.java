package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.SceneManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import java.net.URL;
import java.util.*;

public class SettingsSceneController implements Initializable {

    @FXML private ComboBox<String> languagesComboBox;

    private final Locale frLocal = new Locale("fr", "FR");
    private final Locale enLocal = new Locale("en", "EN");

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        // add items to ComboBox
        languagesComboBox.getItems().add(resources.getString("French"));
        languagesComboBox.getItems().add(resources.getString("English"));

        // set default
        if (resources.getLocale().equals(frLocal)) {
            languagesComboBox.getSelectionModel().select(resources.getString("French"));
        }
        if (resources.getLocale().equals(enLocal)) {
            languagesComboBox.getSelectionModel().select(resources.getString("English"));
        }
    }

    @FXML
    private void onNewLanguageSelected() {
        switch (languagesComboBox.getSelectionModel().getSelectedIndex()) {
            case 0 -> SceneManager.getSceneManager().changeLocale(frLocal);
            case 1 -> SceneManager.getSceneManager().changeLocale(enLocal);
        }
    }
}
