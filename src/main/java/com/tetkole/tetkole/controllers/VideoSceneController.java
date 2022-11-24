package com.tetkole.tetkole.controllers;
import com.tetkole.tetkole.utils.SceneManager;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import com.tetkole.tetkole.utils.RecordManager;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;


import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;

public class VideoSceneController implements Initializable {

    private String videoFileName;
    private RecordManager recordManager;

    private MediaPlayer mediaPlayer;

    @FXML
    private MediaView mediaView;

    @FXML
    private Slider slider;

    @FXML
    private Button btnPlayPause;
    @FXML
    private Button btnRecord;

    private boolean isDragged = false;

    private ResourceBundle resources;

    @FXML
    private HBox header;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // get the children of header component
        ObservableList<Node> childrenOfHeader = this.header.getChildren();
        // search for the btnHome and add a new onMouseClickListener
        for (var child : childrenOfHeader) {
            if (child.getId() != null && child.getId().equals("btnHome")) {
                child.setOnMouseClicked(event -> {
                    // free all mediaPlayer resources and change scene
                    this.mediaPlayer.dispose();
                    SceneManager.getSceneManager().changeScene("MainMenuScene.fxml");
                });
            }
        }

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

        this.videoFileName = videoFile.getName();


        this.recordManager = new RecordManager();
    }


    @FXML
    private void playPauseVideo() {
        switch (mediaPlayer.getStatus()) {
            case PLAYING -> {
                mediaPlayer.pause();
                btnPlayPause.setText(resources.getString("Play"));
                ((ImageView) btnPlayPause.getGraphic()).setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/play.png")).toExternalForm()));
            }
            case PAUSED, READY, STOPPED -> {
                mediaPlayer.play();
                btnPlayPause.setText(resources.getString("Pause"));
                ((ImageView) btnPlayPause.getGraphic()).setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/pause.png")).toExternalForm()));
            }
        }
    }


    @FXML
    protected void onRecordButtonClick() {
        //Date with specific format
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("(dd-MM-YYYY_HH'h'mm'm'ss's')");
        String formattedDateTime = currentDateTime.format(formatter);
        String recordName = "record"+formattedDateTime+".wav";
        System.out.println("Formatted LocalDateTime : " + formattedDateTime);

        if(recordManager.isRecording()) {
            this.recordManager.stopRecording();
            btnRecord.setText(resources.getString("StartRecord"));
            ((ImageView) btnRecord.getGraphic()).setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/record.png")).toExternalForm()));
        } else {
            this.recordManager.startRecording(videoFileName,recordName);
            btnRecord.setText(resources.getString("StopRecord"));
            ((ImageView) btnRecord.getGraphic()).setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/stopRecord.png")).toExternalForm()));
        }
    }
}
