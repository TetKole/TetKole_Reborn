package com.tetkole.tetkole.controllers.modals;

import com.tetkole.tetkole.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AudioDescriptionEditSceneController implements Initializable {

    public TextField descriptionTextField;
    public Label descriptionModalTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.descriptionTextField.setText(SceneManager.getSceneManager().getModalParameterValue());
        this.descriptionModalTextField.setText(SceneManager.getSceneManager().getModalDescriptionValue());
    }

    public void OnConfirmButtonClick(ActionEvent actionEvent) {
        SceneManager.getSceneManager().closeModal(this.getDescription());
    }

    public String getDescription() {
        return this.descriptionTextField.getText();
    }

}
