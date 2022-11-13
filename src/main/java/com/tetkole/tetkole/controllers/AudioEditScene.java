package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.RecordManager;
import com.tetkole.tetkole.utils.SceneManager;
import com.tetkole.tetkole.utils.wave.WaveFormService;
import com.tetkole.tetkole.utils.wave.WaveVisualization;
import javafx.beans.InvalidationListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class AudioEditScene {

    @FXML
    Slider audioSlider;
    @FXML
    WaveVisualization waveVisualization;

    private boolean audioSliderDragged = false;
    // private String audioFileName;
    private MediaPlayer mediaPlayer;
    private RecordManager recordManager;


    @FXML
    protected void initialize() {
        this.recordManager = new RecordManager();

        File audioFile = (File) SceneManager.getSceneManager().getArgument("loaded_file_audio");
        //this.audioFileName = audioFile.getName();
        Media audioMedia = new Media(audioFile.toURI().toString());
        mediaPlayer = new MediaPlayer(audioMedia);


        mediaPlayer.setOnReady(() -> {
            audioSlider.setMin(0);
            audioSlider.setMax(this.mediaPlayer.getTotalDuration().toSeconds());

            audioSlider.setValue(0);
            audioSlider.setBlockIncrement(5);

            // listener for moving slider
            InvalidationListener sliderChangeListener = observable -> audioSlider.setValue(mediaPlayer.currentTimeProperty().getValue().toSeconds());
            mediaPlayer.currentTimeProperty().addListener(sliderChangeListener);


            audioSlider.setOnDragDetected((EventHandler<Event>) event -> {
                audioSliderDragged=true;
                mediaPlayer.currentTimeProperty().removeListener(sliderChangeListener);
            });

            audioSlider.setOnMousePressed((EventHandler<Event>) event -> {
                mediaPlayer.currentTimeProperty().removeListener(sliderChangeListener);
                mediaPlayer.seek(Duration.seconds(audioSlider.getValue()));
                mediaPlayer.currentTimeProperty().addListener(sliderChangeListener);
            });

            audioSlider.setOnMouseReleased((EventHandler<Event>) event -> {
                if(audioSliderDragged){
                    mediaPlayer.seek(Duration.seconds(audioSlider.getValue()));
                    audioSliderDragged=false;
                    mediaPlayer.currentTimeProperty().addListener(sliderChangeListener);
                }
            });
        });


        waveVisualization.getWaveService().startService(audioFile.getAbsolutePath(), WaveFormService.WaveFormJob.AMPLITUDES_AND_WAVEFORM);
    }

    @FXML
    protected void onPlayPauseButtonClick() {
        switch (mediaPlayer.getStatus()) {
            case PLAYING -> mediaPlayer.pause();
            case PAUSED, READY -> mediaPlayer.play();
        }
    }

    @FXML
    protected void onBackButtonClick() {
        mediaPlayer.stop();
        this.recordManager.stopRecording();
        SceneManager.getSceneManager().changeScene("main_menu.fxml");
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
