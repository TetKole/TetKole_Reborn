package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.SceneManager;

import com.tetkole.tetkole.utils.StaticEnvVariable;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.*;

public class SettingsSceneController implements Initializable {

    @FXML private ComboBox<String> languagesComboBox;

    @FXML
    private TextField rangeInput;

    private final Locale frLocal = new Locale("fr", "FR");
    private final Locale enLocal = new Locale("en", "EN");

    @FXML
    private HBox header;

    private int lastMax;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        lastMax = StaticEnvVariable.zoomRange;
        rangeInput.setText(String.valueOf(StaticEnvVariable.zoomRange));

        // get the children of header component
        ObservableList<Node> childrenOfHeader = this.header.getChildren();
        // search for the btnHome and add a new onMouseClickListener
        for (var child : childrenOfHeader) {
            if (child.getId() != null && child.getId().equals("btnHome")) {
                child.setOnMouseClicked(event -> SceneManager.getSceneManager().changeScene("HomeScene.fxml"));
            }
        }


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

    @FXML
    public void OnChangeRange() {
        if(rangeInput.getText().matches("(^(1500|1[0-4][0-9][0-9]|[1-9][0-9][0-9]|[5-9][0-9])$)")) {
            StaticEnvVariable.zoomRange = Integer.parseInt(rangeInput.getText());
            lastMax = StaticEnvVariable.zoomRange;
        } else {
            rangeInput.setText(String.valueOf(lastMax));
        }
    }
}
