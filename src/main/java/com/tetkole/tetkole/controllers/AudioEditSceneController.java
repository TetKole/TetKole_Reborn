package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.components.CustomButton;
import com.tetkole.tetkole.utils.*;
import com.tetkole.tetkole.utils.annotations.AnnotationsVisualization;
import com.tetkole.tetkole.utils.enums.ToastTypes;
import com.tetkole.tetkole.utils.models.Annotation;
import com.tetkole.tetkole.utils.models.FieldAudio;
import com.tetkole.tetkole.utils.wave.WaveFormService;
import com.tetkole.tetkole.utils.wave.WaveVisualization;
import com.tetkole.tetkole.utils.models.Corpus;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
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
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class AudioEditSceneController implements PropertyChangeListener, Initializable {
    public Button btnEditDescription;
    public Button btnChangeTire;
    public HBox bottomHbox;
    private ResourceBundle resources;
    private FieldAudio fieldAudio;
    private Corpus corpus;
    private MediaPlayer mediaPlayer;
    private RecordManager recordManager;
    private int borderSize = StaticEnvVariable.borderSize;
    private List<HBox> lines = new ArrayList<>();
    private int currentTire = 0;

    // Graphics
    @FXML
    WaveVisualization waveVisualization;
    @FXML
    AnnotationsVisualization annotationsVisualization;
    @FXML
    private Button btnRecord;
    @FXML
    private Button btnPlayPause;
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
    @FXML
    private StackPane rootPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.startLoading();
        this.resources = resources;
        this.annotationsVisualization.setEditSceneController(this);

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
        this.corpus = (Corpus) SceneManager.getSceneManager().getArgument("corpus");
        this.fieldAudio = (FieldAudio) SceneManager.getSceneManager().getArgument("fieldAudio");

        File audioFile = this.fieldAudio.getFile();
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
                this.setBtnToPlay();
            });

            this.settingUpSidePane();
        });

        this.waveVisualization.startVisualization(audioFile.getAbsolutePath(), WaveFormService.WaveFormJob.AMPLITUDES_AND_WAVEFORM, this);
        this.waveVisualization.addPropertyChangeListener(this);

        //We set the annotationsVisualization width when the screen size change
        this.container.widthProperty().addListener((observable, oldValue, newValue) -> {
            this.annotationsVisualization.setPrefWidth((double) newValue);
            this.annotationsVisualization.setValueFromWave(this.waveVisualization.getRatioAudio(),
                    this.waveVisualization.getBeginAudio(),
                    this.waveVisualization.getEndAudio());
        });

        //We set the annotationsVisualization height to handle 30% of the screen height
        this.centerAnchorPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            AnchorPane.setBottomAnchor(this.waveVisualization, this.centerAnchorPane.getHeight() * 0.3);
            this.annotationsVisualization.setPrefHeight(this.centerAnchorPane.getHeight() * 0.3);
        });

        bottomHbox.setPrefHeight(SceneManager.getSceneManager().getStageHeight() * 0.03);
    }

    private void settingUpAnnotations() {
        this.annotationsVisualization.setValueFromWave(
                this.waveVisualization.getRatioAudio(),
                this.waveVisualization.getBeginAudio(),
                this.waveVisualization.getEndAudio()
        );
        this.annotationsVisualization.drawAnnotations();
    }

    @FXML
    protected void onPlayPauseButtonClick() {
        switch (mediaPlayer.getStatus()) {
            case PLAYING -> {
                mediaPlayer.setStartTime(new Duration((waveVisualization.getCurrentXPosition() / waveVisualization.getRatioAudio() + waveVisualization.getBeginAudio()) * 1000));
                mediaPlayer.pause();
                this.setBtnToPlay();
            }
            case PAUSED, READY, STOPPED -> {
                mediaPlayer.play();
                this.setBtnToPause();
            }
        }
    }

    @FXML
    protected void onRecordButtonClick() {
        if (!recordManager.isRecording()) {
            this.lines.forEach(hBox -> {
                hBox.getChildren().get(1).setDisable(true);
            });
            this.annotationsVisualization.getAnnotationsRectanglesMenu().forEach(hBox -> {
                hBox.getChildren().get(1).setDisable(true);
            });
            // get the date for the record name
            String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("(dd-MM-yyyy_HH'h'mm'm'ss's'_SSS)"));
            formattedDateTime = formattedDateTime.substring(1, formattedDateTime.length() - 1);
            String recordName = "annotation_" + formattedDateTime + ".wav";

            String corpusPath = "/" + this.corpus.getName() + "/" + Corpus.folderNameAnnotation + "/" + this.fieldAudio.getName();
            this.recordManager.startRecording(this.fieldAudio.getName(), recordName, corpusPath, this.waveVisualization.getRightBorderTime(), this.waveVisualization.getLeftBorderTime());

            btnRecord.setText(resources.getString("StopRecord"));
            ((ImageView) btnRecord.getGraphic()).setImage(
                    new Image(Objects.requireNonNull(getClass().getResource("/images/stopRecord.png")).toExternalForm())
            );
        } else {
            this.lines.forEach(hBox -> {
                hBox.getChildren().get(1).setDisable(false);
            });
            this.annotationsVisualization.getAnnotationsRectanglesMenu().forEach(hBox -> {
                hBox.getChildren().get(1).setDisable(false);
            });
            this.recordManager.stopRecording(this.fieldAudio, this.currentTire);
            this.settingUpSidePane();

            btnRecord.setText(resources.getString("StartRecord"));
            ((ImageView) btnRecord.getGraphic()).setImage(
                    new Image(Objects.requireNonNull(getClass().getResource("/images/record.png")).toExternalForm())
            );
        }
    }

    public void onScroll(ScrollEvent scrollEvent) {
        //print scroll event
        this.mediaPlayer.stop();
        this.waveVisualization.setRangeZoom(scrollEvent);
        this.annotationsVisualization.setValueFromWave(this.waveVisualization.getRatioAudio(),
                this.waveVisualization.getBeginAudio(),
                this.waveVisualization.getEndAudio());
        this.annotationsVisualization.refresh();
    }

    public void goToAnnotation(double begin, double end) {
        this.mediaPlayer.stop();
        this.waveVisualization.setLeftBorderTime(begin);
        this.waveVisualization.setRightBorderTime(end);

        this.waveVisualization.unZoom();
        this.waveVisualization.setRangeZoomFromAnnotation();
        this.annotationsVisualization.setValueFromWave(this.waveVisualization.getRatioAudio(),
                this.waveVisualization.getBeginAudio(),
                this.waveVisualization.getEndAudio());
        this.annotationsVisualization.refresh();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        mediaPlayer.stop();
        this.setBtnToPlay();

        // set current X in media player aka start time
        double newStart = (double) evt.getNewValue() / waveVisualization.getRatioAudio() + waveVisualization.getBeginAudio();
        mediaPlayer.setStartTime(new Duration((newStart) * 1000));
        // set stop time (with right border)
        double newStop = (waveVisualization.getRightBorderXPosition() + borderSize) / waveVisualization.getRatioAudio() + waveVisualization.getBeginAudio();
        mediaPlayer.setStopTime(new Duration((newStop) * 1000));
    }

    public void handleRecord(Annotation annotation, HBox line) {
        CustomButton btnRecord = ((CustomButton) line.getChildren().get(1));
        int index = this.fieldAudio.getAnnotations().indexOf(annotation);

        if (!recordManager.isRecording()) {

            this.btnRecord.setDisable(true);
            btnRecord.setImage(Objects.requireNonNull(getClass().getResource("/images/stopRecord.png")).toExternalForm());
            ((CustomButton) this.annotationsVisualization.getAnnotationsRectanglesMenu().get(index).getChildren().get(1)).setImage(
                    Objects.requireNonNull(getClass().getResource("/images/stopRecord.png")).toExternalForm()
            );
            // get the date for the record name
            String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("(dd-MM-yyyy_HH'h'mm'm'ss's'_SSS)"));
            formattedDateTime = formattedDateTime.substring(1, formattedDateTime.length() - 1);
            String recordName = "newAnnotation_" + formattedDateTime + ".wav";

            String corpusPath = "/" + this.corpus.getName() + "/" + Corpus.folderNameAnnotation + "/" + this.fieldAudio.getName();
            this.recordManager.startRecording(this.fieldAudio.getName(), recordName, corpusPath, annotation.getEnd(), annotation.getStart());
        } else {
            this.btnRecord.setDisable(false);
            btnRecord.setImage(Objects.requireNonNull(getClass().getResource("/images/reRecord.png")).toExternalForm());
            ((CustomButton) this.annotationsVisualization.getAnnotationsRectanglesMenu().get(index).getChildren().get(0)).setImage(
                    Objects.requireNonNull(getClass().getResource("/images/reRecord.png")).toExternalForm()
            );

            this.recordManager.stopRecording(this.fieldAudio, annotation.getTire());
            this.lines.remove(line);
            this.setupLine(this.fieldAudio.getAnnotations().get(this.fieldAudio.getAnnotations().size() - 1));
            this.vBoxPane.getChildren().remove(line);
            this.fieldAudio.deleteAnnotation(annotation);
            this.fieldAudio.getAnnotations().remove(annotation);
            this.annotationsVisualization.refresh();
        }
    }

    private void settingUpSidePane() {

        vBoxPane.setSpacing(50);

        vBoxPane.getChildren().clear();

        this.lines.clear();
        for (Annotation annotation : this.fieldAudio.getAnnotations()) {
            this.setupLine(annotation);
        }

        this.settingUpAnnotations();

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

    public void setupLine(Annotation annotation) {
        // prepare the HBox
        HBox line = new HBox();
        line.getStyleClass().add("line");
        line.setAlignment(Pos.CENTER);
        line.setSpacing(20);

        // add the DeleteButton
        CustomButton btnDelete = new CustomButton(Objects.requireNonNull(getClass().getResource("/images/trash.png")).toExternalForm());
        line.getChildren().add(btnDelete);

        btnDelete.setOnAction(e -> {
            this.lines.remove(line);
            this.vBoxPane.getChildren().remove(line);
            this.fieldAudio.deleteAnnotation(annotation);
            this.annotationsVisualization.refresh();
        });

        CustomButton btnRecord = new CustomButton(Objects.requireNonNull(getClass().getResource("/images/reRecord.png")).toExternalForm());
        line.getChildren().add(btnRecord);

        btnRecord.setOnAction(e -> handleRecord(annotation, line));

        // add the Label
        Label label = new Label(annotation.getName());
        label.setStyle("-fx-text-fill: white; -fx-text-alignment: CENTER");
        label.setPrefWidth(200);
        label.setWrapText(true);
        line.getChildren().add(label);
        // goToAnnotation on double click from side panel
        label.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                goToAnnotation(annotation.getStart(), annotation.getEnd());
            }
        });

        CustomButton btnEdit = new CustomButton(Objects.requireNonNull(getClass().getResource("/images/edit.png")).toExternalForm());
        line.getChildren().add(btnEdit);

        btnEdit.setOnAction(event -> {
            String[] annotationName = annotation.getName().split("\\.");
            String ext = "." + annotationName[annotationName.length - 1];
            String lastName = annotation.getName().substring(0, annotation.getName().length() - ext.length());
            String newName = SceneManager.getSceneManager().showNewModal("modals/AudioDescriptionEditScene.fxml", lastName, resources.getString("RenameAnnotation"));
            if (!newName.equals(lastName) && !newName.isEmpty()) {
                if(!newName.contains(" ")) {
                    corpus.renameAnnotation(annotation, newName + ext);
                    label.setText(newName + ext);
                    annotationsVisualization.refresh();
                } else {
                    SceneManager.getSceneManager().sendToast(resources.getString("NameNotContainsSpaces"), ToastTypes.ERROR);
                }
            } else {
                SceneManager.getSceneManager().sendToast(resources.getString("NewNameDifferent"), ToastTypes.ERROR);
            }
        });

        // add the Play/Pause Button
        CustomButton btnPlayPauseAnn = new CustomButton(Objects.requireNonNull(getClass().getResource("/images/play.png")).toExternalForm());
        line.getChildren().add(btnPlayPauseAnn);

        btnPlayPauseAnn.setOnAction(e -> {
            annotation.playPause();

            int index = this.fieldAudio.getAnnotations().indexOf(annotation);
            switch (annotation.getMediaPlayer().getStatus()) {
                case PLAYING -> {
                    annotation.getMediaPlayer().stop();
                    btnPlayPauseAnn.setImage(Objects.requireNonNull(getClass().getResource("/images/play.png")).toExternalForm());
                    ((CustomButton) this.annotationsVisualization.getAnnotationsRectanglesMenu().get(index).getChildren().get(0)).setImage(
                            Objects.requireNonNull(getClass().getResource("/images/play.png")).toExternalForm()
                    );
                }
                case PAUSED, READY, STOPPED -> {
                    annotation.getMediaPlayer().play();
                    btnPlayPauseAnn.setImage(Objects.requireNonNull(getClass().getResource("/images/stop.png")).toExternalForm());
                    ((CustomButton) this.annotationsVisualization.getAnnotationsRectanglesMenu().get(index).getChildren().get(0)).setImage(
                            Objects.requireNonNull(getClass().getResource("/images/stop.png")).toExternalForm()
                    );

                    annotation.getMediaPlayer().setOnEndOfMedia(() -> {
                                btnPlayPauseAnn.setImage(Objects.requireNonNull(getClass().getResource("/images/play.png")).toExternalForm());
                                ((CustomButton) this.annotationsVisualization.getAnnotationsRectanglesMenu().get(index).getChildren().get(0)).setImage(
                                        Objects.requireNonNull(getClass().getResource("/images/play.png")).toExternalForm()
                                );
                                annotation.getMediaPlayer().stop();
                            }
                    );
                }
            }
        });

        // add the HBox to the VBox
        this.vBoxPane.getChildren().add(line);
        this.lines.add(line);
    }

    public Corpus getCorpus() {
        return corpus;
    }

    public FieldAudio getFieldAudio() {
        return fieldAudio;
    }

    public List<HBox> getLines() {
        return lines;
    }

    public VBox getvBoxPane() {
        return vBoxPane;
    }

    public void onEditDescriptionButtonClick(ActionEvent actionEvent) {
        String description = SceneManager.getSceneManager().showNewModal("modals/AudioDescriptionEditScene.fxml", this.fieldAudio.getDescription(), resources.getString("EditDescription"));

        this.fieldAudio.setDescription(description);

        Map<String, Object> map = Map.of(
                "description", this.fieldAudio.getDescription()
        );

        FileManager.getFileManager().createJSONFile(this.fieldAudio.getName(), map, String.format("/%s/FieldAudio", this.corpus.getName()));
    }

    private void setBtnToPlay() {
        this.btnPlayPause.setText(resources.getString("Play"));
        ((ImageView) btnPlayPause.getGraphic()).setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/play.png")).toExternalForm()));
    }

    private void setBtnToPause() {
        this.btnPlayPause.setText(resources.getString("Pause"));
        ((ImageView) btnPlayPause.getGraphic()).setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/pause.png")).toExternalForm()));
    }

    public void onChangeTireButtonClick() {
        currentTire = (currentTire + 1) % 5;
        btnChangeTire.setText(String.valueOf(currentTire + 1));
    }

    public void startLoading() {
        LoadingManager.getLoadingManagerInstance().displayLoading(rootPane);
    }

    public void stopLoading() {
        LoadingManager.getLoadingManagerInstance().hideLoading(rootPane);
    }
}
