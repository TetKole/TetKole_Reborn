package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.RecordManager;
import com.tetkole.tetkole.utils.SceneManager;
import com.tetkole.tetkole.utils.wave.WaveFormService;
import com.tetkole.tetkole.utils.wave.WaveVisualization;
import javafx.fxml.FXML;
import javafx.scene.input.ScrollEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class AudioEditSceneController implements PropertyChangeListener {

    @FXML
    WaveVisualization waveVisualization;

    // private String audioFileName;
    private MediaPlayer mediaPlayer;
    private RecordManager recordManager;

    private double totalTime;


    @FXML
    protected void initialize() {
        this.recordManager = new RecordManager();

        File audioFile = (File) SceneManager.getSceneManager().getArgument("loaded_file_audio");
        //this.audioFileName = audioFile.getName();
        Media audioMedia = new Media(audioFile.toURI().toString());
        mediaPlayer = new MediaPlayer(audioMedia);


        mediaPlayer.setOnReady(() -> {
            totalTime = mediaPlayer.getTotalDuration().toSeconds();

            waveVisualization.setTotalTime(mediaPlayer.getTotalDuration().toSeconds());

            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                waveVisualization.setCursorTime(newValue.toSeconds());
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.stop();
            });
        });

        waveVisualization.getWaveService().startService(audioFile.getAbsolutePath(), WaveFormService.WaveFormJob.AMPLITUDES_AND_WAVEFORM);

        waveVisualization.addPropertyChangeListener(this);
    }

    @FXML
    protected void onPlayPauseButtonClick() {
        switch (mediaPlayer.getStatus()) {
            case PLAYING -> mediaPlayer.pause();
            case PAUSED, READY, STOPPED -> mediaPlayer.play();
        }
    }

    // TODO Make the mediaplayer and recordManager stop when clicking in the return button in the header
    /* @FXML
    protected void onBackButtonClick() {
        mediaPlayer.stop();
        this.recordManager.stopRecording();
        SceneManager.getSceneManager().changeScene("MainMenuScene.fxml");
    } */

    @FXML
    protected void onRecordButtonClick() {
        if(recordManager.isRecording()) {
            this.recordManager.stopRecording();
        } else {
            this.recordManager.startRecording();
        }
    }

    public void onScroll(ScrollEvent scrollEvent) {
        // TODO Zoom
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        mediaPlayer.stop();

        // set current X in media player aka start time
        double newCurrentX = (double) evt.getNewValue();
        double widthWave =  waveVisualization.widthProperty().getValue();

        double newStart = newCurrentX * totalTime / widthWave;
        mediaPlayer.setStartTime(new Duration(newStart * 1000));

        // set stop time (with right border)
        double rightBorderXPosition = waveVisualization.getRightBorderXPosition() + 10;
        double newStop = rightBorderXPosition * totalTime / widthWave;
        mediaPlayer.setStopTime(new Duration(newStop * 1000));
    }
}
