package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.JsonManager;
import com.tetkole.tetkole.utils.RecordManager;
import com.tetkole.tetkole.utils.SceneManager;
import com.tetkole.tetkole.utils.annotations.AnnotationsVisualization;
import com.tetkole.tetkole.utils.models.Annotation;
import com.tetkole.tetkole.utils.wave.WaveFormService;
import com.tetkole.tetkole.utils.wave.WaveVisualization;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Screen;
import javafx.util.Duration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class AudioEditSceneController implements PropertyChangeListener, Initializable {

    @FXML
    WaveVisualization waveVisualization;
    @FXML
    AnnotationsVisualization annotationsVisualization;
    JsonManager jsonManager;
    private String audioFileName;
    private MediaPlayer mediaPlayer;
    private RecordManager recordManager;
    @FXML
    private Button btnRecord;
    @FXML
    private Button btnPlayPause;
    private ResourceBundle resources;
    @FXML
    private HBox header;
    @FXML
    private Button btnDisplaySidePane;
    @FXML
    private AnchorPane recordAnchorPane;
    @FXML
    private AnchorPane centerAnchorPane;
    @FXML
    private BorderPane container;
    @FXML
    private VBox vBoxPane;

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

        this.jsonManager = new JsonManager();

        File audioFile = (File) SceneManager.getSceneManager().getArgument("loaded_file_audio");
        this.audioFileName = audioFile.getName();
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

            this.settingUpSidePane();
            //TODO Vrai liste
            List<Annotation> annotations = new ArrayList<>();
            annotations.add(new Annotation(0,5));
            annotations.add(new Annotation(10,20));
            annotations.add(new Annotation(30,60));
            this.annotationsVisualization.setAnnotations(annotations);
            this.annotationsVisualization.setValueFromWave(this.waveVisualization.getRatioAudio(),
                    this.waveVisualization.getBeginAudio(),
                    this.waveVisualization.getEndAudio());
            this.annotationsVisualization.drawAnnotations();
        });

        this.waveVisualization.startVisualization(audioFile.getAbsolutePath(), WaveFormService.WaveFormJob.AMPLITUDES_AND_WAVEFORM);
        this.waveVisualization.addPropertyChangeListener(this);

        //We set the annotationsVisualization width when the screen size change
        this.container.widthProperty().addListener((observable, oldValue, newValue) -> {
            this.annotationsVisualization.setWidth((double)newValue);
            this.annotationsVisualization.setValueFromWave(this.waveVisualization.getRatioAudio(),
                    this.waveVisualization.getBeginAudio(),
                    this.waveVisualization.getEndAudio());
        });

        //We set the annotationsVisualization height to handle 30% of the screen height
        this.centerAnchorPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            AnchorPane.setBottomAnchor(this.waveVisualization, this.centerAnchorPane.getHeight() * 0.1);
            this.annotationsVisualization.setHeight(this.centerAnchorPane.getHeight() * 0.1);
        });
    }

    @FXML
    protected void onPlayPauseButtonClick() {
        switch (mediaPlayer.getStatus()) {
            case PLAYING -> {
                mediaPlayer.setStartTime(new Duration((waveVisualization.getCurrentXPosition() / waveVisualization.getRatioAudio() + waveVisualization.getBeginAudio()) * 1000));
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
            this.jsonManager.saveJson(audioFileName,recordName,waveVisualization.getLeftBorderTime(),waveVisualization.getRightBorderTime());
            this.recordManager.stopRecording();
            btnRecord.setText(resources.getString("StartRecord"));
            ((ImageView) btnRecord.getGraphic()).setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/record.png")).toExternalForm()));
        } else {
            this.recordManager.startRecording(audioFileName,recordName);
            btnRecord.setText(resources.getString("StopRecord"));
            ((ImageView) btnRecord.getGraphic()).setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/stopRecord.png")).toExternalForm()));
        }
    }

    public void onScroll(ScrollEvent scrollEvent) {
        this.mediaPlayer.stop();
        this.waveVisualization.setRangeZoom(scrollEvent);
        this.annotationsVisualization.setValueFromWave(this.waveVisualization.getRatioAudio(),
                this.waveVisualization.getBeginAudio(),
                this.waveVisualization.getEndAudio());
        this.annotationsVisualization.drawAnnotations();
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

        //annotationsVisualization.setRatioAudio(waveVisualization.getRatioAudio());
    }

    private void settingUpSidePane() {
        // test values */
        String[] values = {
                "record-1-14.15",
                "record-2-16.17",
                "record-3-19.45",
                "record-4-20.52",
                "record-5-34.08",
                "record-1-14.15",
                "record-2-16.17",
                "record-3-19.45",
                "record-4-20.52",
                "record-5-34.08",
                "record-1-14.15",
                "record-2-16.17",
                "record-3-19.45",
                "record-4-20.52",
                "record-5-34.08",
                "record-1-14.15",
                "record-2-16.17",
                "record-3-19.45",
                "record-4-20.52",
                "record-5-34.08",
                "record-1-14.15",
                "record-2-16.17",
                "record-3-19.45",
                "record-4-20.52",
                "record-5-34.08",
                "record-1-14.15",
                "record-2-16.17",
                "record-3-19.45",
                "record-4-20.52",
                "record-5-34.08",
                "record-1-14.15",
                "record-2-16.17",
                "record-3-19.45",
                "record-4-20.52",
                "record-5-34.08"
        };
        //*************/

        vBoxPane.setSpacing(50);


        for(String value : values) {
            // prepare the HBox
            HBox line = new HBox();
            line.setAlignment(Pos.CENTER);
            line.setSpacing(50);

            // add the DeleteButton
            Button btnDelete = new Button("Delete");
            btnDelete.getStyleClass().add("buttons");
            btnDelete.getStyleClass().add("blue");
            line.getChildren().add(btnDelete);

            // add the Label
            Label label = new Label(value);
            label.setStyle("-fx-text-fill: white;");
            line.getChildren().add(label);

            // add the Play/Pause Button
            Button btnPlayPause = new Button("Play");
            btnPlayPause.getStyleClass().add("buttons");
            btnPlayPause.getStyleClass().add("blue");
            line.getChildren().add(btnPlayPause);

            // add the HBox to the VBox
            vBoxPane.getChildren().add(line);
        }



        ChangeListener listener = (observable, oldValue, newValue) -> {
            recordAnchorPane.setTranslateX(-recordAnchorPane.getWidth());
        };

        recordAnchorPane.widthProperty().addListener(listener);
        recordAnchorPane.setPrefWidth(Screen.getPrimary().getBounds().getWidth() / 4);



        // animated transitions
        TranslateTransition openNav = new TranslateTransition(new Duration(350), recordAnchorPane);
        openNav.setToX(0);
        TranslateTransition closeNav = new TranslateTransition(new Duration(350), recordAnchorPane);

        // on button click
        btnDisplaySidePane.setOnAction(e -> {
            if (recordAnchorPane.getTranslateX() != 0) {
                openNav.play();
            } else {
                // delete listener on the first close (called every times java doesn't cares)
                recordAnchorPane.widthProperty().removeListener(listener);

                closeNav.setToX(-(recordAnchorPane.getWidth()));
                closeNav.play();
            }
        });
    }
}
