package com.tetkole.tetkole.controllers;
import com.tetkole.tetkole.utils.SceneManager;

import com.tetkole.tetkole.utils.models.Corpus;
import com.tetkole.tetkole.utils.models.CorpusVideo;
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
    private CorpusVideo video;
    private Corpus corpus;
    private boolean isDragged = false;
    private ResourceBundle resources;


    // Graphics
    @FXML
    private MediaView mediaView;
    @FXML
    private Slider slider;
    @FXML
    private Button btnPlayPause;
    @FXML
    private Button btnRecord;
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
                    SceneManager.getSceneManager().addArgument("corpus", this.corpus);
                    SceneManager.getSceneManager().changeScene("CorpusMenuScene.fxml");
                });
            }
        }


        this.corpus = (Corpus) SceneManager.getSceneManager().getArgument("corpus");
        this.video = (CorpusVideo) SceneManager.getSceneManager().getArgument("video");
        File videoFile = this.video.getFile();

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
        if (!recordManager.isRecording()) {
            // get the date for the record name
            String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("(dd-MM-yyyy_HH'h'mm'm'ss's')"));
            formattedDateTime = formattedDateTime.substring(1, formattedDateTime.length()-1);
            String recordName = "annotation_" + formattedDateTime + ".wav";

            String corpusPath = "/" + this.corpus.getName() + "/" + Corpus.folderNameAnnotation + "/" + this.video.getName();
            this.recordManager.startRecording(this.video.getName(), recordName, corpusPath, this.mediaPlayer.getCurrentTime().toSeconds(), this.mediaPlayer.getTotalDuration().toSeconds());

            btnRecord.setText(resources.getString("StopRecord"));
            ((ImageView) btnRecord.getGraphic()).setImage(
                    new Image(Objects.requireNonNull(getClass().getResource("/images/stopRecord.png")).toExternalForm())
            );
        }
        else
        {
            this.recordManager.stopRecording(this.video);

            btnRecord.setText(resources.getString("StartRecord"));
            ((ImageView) btnRecord.getGraphic()).setImage(
                    new Image(Objects.requireNonNull(getClass().getResource("/images/record.png")).toExternalForm())
            );
        }
    }
}
