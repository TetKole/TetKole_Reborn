package com.tetkole.tetkole.controllers.modals;

import com.tetkole.tetkole.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class AlertModalController implements Initializable {
    @FXML
    public Label descriptionLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.descriptionLabel.setText(SceneManager.getSceneManager().getModalParameterValue());
    }

    public void OnConfirmButtonClick(ActionEvent actionEvent) {
        SceneManager.getSceneManager().closeAlertModal();
    }

}
