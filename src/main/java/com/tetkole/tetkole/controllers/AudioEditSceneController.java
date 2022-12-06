package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.JsonManager;
import com.tetkole.tetkole.utils.RecordManager;
import com.tetkole.tetkole.utils.SceneManager;
import com.tetkole.tetkole.utils.models.Corpus;
import com.tetkole.tetkole.utils.models.FieldAudio;
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
import java.util.Objects;
import java.util.ResourceBundle;

public class AudioEditSceneController implements PropertyChangeListener, Initializable {

    @FXML
    WaveVisualization waveVisualization;

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
    private AnchorPane pane;
    @FXML
    private VBox vBoxPane;

    private FieldAudio fieldAudio;
    private Corpus corpus;

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

        this.recordManager = new RecordManager();

        this.jsonManager = new JsonManager();

        this.corpus = (Corpus) SceneManager.getSceneManager().getArgument("corpus");
        this.fieldAudio = (FieldAudio) SceneManager.getSceneManager().getArgument("fieldAudio");
        File audioFile = this.fieldAudio.getFile();

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
        });

        waveVisualization.startVisualization(audioFile.getAbsolutePath(), WaveFormService.WaveFormJob.AMPLITUDES_AND_WAVEFORM);

        waveVisualization.addPropertyChangeListener(this);
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



        ChangeListener listener = (observable, oldValue, newValue) -> pane.setTranslateX(-pane.getWidth());

        pane.widthProperty().addListener(listener);
        pane.setPrefWidth(Screen.getPrimary().getBounds().getWidth() / 4);



        // animated transitions
        TranslateTransition openNav = new TranslateTransition(new Duration(350), pane);
        openNav.setToX(0);
        TranslateTransition closeNav = new TranslateTransition(new Duration(350), pane);

        // on button click
        btnDisplaySidePane.setOnAction(e -> {
            if (pane.getTranslateX() != 0) {
                openNav.play();
            } else {
                // delete listener on the first close (called every times java doesn't cares)
                pane.widthProperty().removeListener(listener);

                closeNav.setToX(-(pane.getWidth()));
                closeNav.play();
            }
        });
    }
}
