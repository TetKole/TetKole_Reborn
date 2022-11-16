package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.SceneManager;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.tetkole.tetkole.utils.RecordManager;


import java.io.File;

public class ImageRecordSceneController {
    Image image;
    @FXML
    ImageView imageView;

    private RecordManager recordManager;

    @FXML
    protected void onBackButtonClick() {
        SceneManager.getSceneManager().changeScene("MainMenuScene.fxml");
    }

    //Initialize the scene
    @FXML
    protected void initialize() {
        this.recordManager = new RecordManager();

        //Get the file from the arguments and into image to show them in fxml
        File imageFile = (File) SceneManager.getSceneManager().getArgument("loaded_file_image");
        image = new Image(imageFile.toURI().toString());
        imageView.setImage(image);
    }

    @FXML
    protected void onRecordButtonClick() {
        if(recordManager.isRecording()) {
            this.recordManager.stopRecording();
        } else {
            this.recordManager.startRecording();
        }
    }
}
