package com.tetkole.tetkole.controllers;
import com.tetkole.tetkole.utils.SceneManager;

import javafx.beans.InvalidationListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import com.tetkole.tetkole.utils.RecordManager;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;


import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class VideoSceneController implements Initializable {
    private RecordManager recordManager;

    private MediaPlayer mediaPlayer;

    @FXML
    private MediaView mediaView;

    @FXML
    private Slider slider;

    @FXML
    private Button btnPlayPause;

    private boolean isDragged = false;

    private ResourceBundle resources;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        File videoFile = (File) SceneManager.getSceneManager().getArgument("loaded_file_video");

        Media media = new Media(videoFile.toURI().toString());
        this.mediaPlayer = new MediaPlayer(media);
        this.mediaView.setMediaPlayer(this.mediaPlayer);

        // When mediaPlayer is loaded, configure it
        mediaPlayer.setOnReady(() -> {
            this.slider.setMin(0);
            this.slider.setMax(this.mediaPlayer.getTotalDuration().toSeconds());

            this.slider.setValue(0);
            this.slider.setBlockIncrement(5);

            // listener for moving slider
            InvalidationListener sliderChangeListener = observable -> slider.setValue(mediaPlayer.currentTimeProperty().getValue().toSeconds());
            mediaPlayer.currentTimeProperty().addListener(sliderChangeListener);


            slider.setOnDragDetected((EventHandler<Event>) event -> {
                isDragged=true;
                mediaPlayer.currentTimeProperty().removeListener(sliderChangeListener);
            });

            slider.setOnMousePressed((EventHandler<Event>) event -> {
                mediaPlayer.currentTimeProperty().removeListener(sliderChangeListener);
                mediaPlayer.seek(Duration.seconds(slider.getValue()));
                mediaPlayer.currentTimeProperty().addListener(sliderChangeListener);
            });

            slider.setOnMouseReleased((EventHandler<Event>) event -> {
                if(isDragged){
                    mediaPlayer.seek(Duration.seconds(slider.getValue()));
                    isDragged = false;
                    mediaPlayer.currentTimeProperty().addListener(sliderChangeListener);
                }
            });
        });

        this.recordManager = new RecordManager();
    }


    @FXML
    private void playPauseVideo() {
        switch (mediaPlayer.getStatus()) {
            case PLAYING -> {
                mediaPlayer.pause();
                btnPlayPause.setText(resources.getString("Play"));
                //TODO find images URL for buttons
                //((ImageView) btnPlayPause.getGraphic()).setImage(new Image("@../../../../../resources/images/play.png"));
            }
            case PAUSED, READY, STOPPED -> {
                mediaPlayer.play();
                btnPlayPause.setText(resources.getString("Pause"));
                //((ImageView) btnPlayPause.getGraphic()).setImage(new Image("@../../../../../resources/images/pause.png"));
            }
        }
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
