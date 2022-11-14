package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Locale;

public class MainMenu {

    @FXML
    protected void onAudioButtonClick() {
            FileChooser fileChooserAudio = new FileChooser();
            fileChooserAudio.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav")
            );
            File fileAudio = fileChooserAudio.showOpenDialog(null);
            if (fileAudio != null){
                SceneManager.getSceneManager().addArgument("loaded_file_audio", fileAudio);
                SceneManager.getSceneManager().changeScene("audio_edit_scene.fxml");
            } else {
                Label audioErrorLabel = new Label("Ce fichier ne peut pas être chargé");
                Alert alert = new Alert(Alert.AlertType.ERROR, audioErrorLabel.getText());
                alert.showAndWait();
            }
    }

    @FXML
    protected void onImageButtonClick() {
        //SceneManager.getSceneManager().changeScene("audio_edit_scene.fxml");
        System.out.println("todo");
    }

    @FXML
    protected void onVideoButtonClick() {
        //SceneManager.getSceneManager().changeScene("audio_edit_scene.fxml");
        System.out.println("todo");
    }

    @FXML
    protected void onSettingsButtonClick() {
        SceneManager.getSceneManager().changeScene("setting.fxml");
    }

    @FXML
    protected void onDoSomething() {
        System.out.println("click");
    }

}