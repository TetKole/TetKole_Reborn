package com.tetkole.tetkole.controllers.modals;

import com.tetkole.tetkole.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AudioDescriptionEditSceneController implements Initializable {

    public TextField descriptionTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.descriptionTextField.setText(SceneManager.getSceneManager().getModalParameterValue());
    }

    public void OnConfirmButtonClick(ActionEvent actionEvent) {
        SceneManager.getSceneManager().closeModal(this.getDescription());
    }

    public String getDescription() {
        return this.descriptionTextField.getText();
    }

}
