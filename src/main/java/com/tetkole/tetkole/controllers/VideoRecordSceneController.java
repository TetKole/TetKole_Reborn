package com.tetkole.tetkole.controllers;
import com.tetkole.tetkole.utils.SceneManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.tetkole.tetkole.utils.RecordManager;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;


import java.io.File;

public class VideoRecordSceneController {
    private RecordManager recordManager;

    private MediaPlayer mediaPlayer;

    @FXML
    private MediaView mediaView;


    @FXML
    protected void initialize() {
        this.recordManager = new RecordManager();

        File videoFile = (File) SceneManager.getSceneManager().getArgument("loaded_file_video");

        Media media = new Media(videoFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        mediaPlayer.play();


    }

    @FXML
    private void playVid(ActionEvent event) {
        mediaPlayer.play();
        mediaPlayer.setRate(1);
    }

    @FXML
    private void pauseVid(ActionEvent event) {
        mediaPlayer.pause();
    }

    @FXML
    private void stopVid(ActionEvent event) {
        mediaPlayer.stop();
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
