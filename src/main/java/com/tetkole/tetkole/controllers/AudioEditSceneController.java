package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.RecordManager;
import com.tetkole.tetkole.utils.SceneManager;
import com.tetkole.tetkole.utils.wave.WaveFormService;
import com.tetkole.tetkole.utils.wave.WaveVisualization;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class AudioEditSceneController implements PropertyChangeListener, Initializable {

    @FXML
    WaveVisualization waveVisualization;
    // private String audioFileName;
    private MediaPlayer mediaPlayer;
    private RecordManager recordManager;
    @FXML
    private Button btnRecord;
    @FXML
    private Button btnPlayPause;
    private ResourceBundle resources;
    @FXML
    private HBox header;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

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

        this.recordManager = new RecordManager();

        File audioFile = (File) SceneManager.getSceneManager().getArgument("loaded_file_audio");
        //this.audioFileName = audioFile.getName();
        Media audioMedia = new Media(audioFile.toURI().toString());
        mediaPlayer = new MediaPlayer(audioMedia);


        mediaPlayer.setOnReady(() -> {

            waveVisualization.setTotalTime(mediaPlayer.getTotalDuration().toSeconds());

            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                waveVisualization.setCursorTime(newValue.toSeconds());
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.stop();
            });
        });

        waveVisualization.startVisualization(audioFile.getAbsolutePath(), WaveFormService.WaveFormJob.AMPLITUDES_AND_WAVEFORM);

        waveVisualization.addPropertyChangeListener(this);
    }

    @FXML
    protected void onPlayPauseButtonClick() {
        switch (mediaPlayer.getStatus()) {
            case PLAYING -> {
                mediaPlayer.setStartTime(new Duration((waveVisualization.getCurrentXPosition() / waveVisualization.getRatioAudio()) * 1000));
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
        if(recordManager.isRecording()) {
            this.recordManager.stopRecording();
            btnRecord.setText(resources.getString("StartRecord"));
            ((ImageView) btnRecord.getGraphic()).setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/record.png")).toExternalForm()));
        } else {
            this.recordManager.startRecording();
            btnRecord.setText(resources.getString("StopRecord"));
            ((ImageView) btnRecord.getGraphic()).setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/stopRecord.png")).toExternalForm()));
        }
    }

    public void onScroll(ScrollEvent scrollEvent) {
        mediaPlayer.stop();
        this.waveVisualization.setRangeZoom(scrollEvent);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        mediaPlayer.stop();
        btnPlayPause.setText(resources.getString("Play"));
        ((ImageView) btnPlayPause.getGraphic()).setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/play.png")).toExternalForm()));

        // set current X in media player aka start time
        double newCurrentX = (double) evt.getNewValue();
        double newStart = newCurrentX / waveVisualization.getRatioAudio() + waveVisualization.getBeginAudio();
        mediaPlayer.setStartTime(new Duration((newStart) * 1000));
        // set stop time (with right border)
        double rightBorderXPosition = waveVisualization.getRightBorderXPosition() + 10;
        double newStop = rightBorderXPosition / waveVisualization.getRatioAudio() + waveVisualization.getBeginAudio();
        mediaPlayer.setStopTime(new Duration((newStop) * 1000));
    }
}
