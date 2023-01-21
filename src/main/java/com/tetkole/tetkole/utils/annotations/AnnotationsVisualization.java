package com.tetkole.tetkole.utils.annotations;

import com.tetkole.tetkole.utils.RecordManager;
import com.tetkole.tetkole.utils.models.Annotation;
import com.tetkole.tetkole.utils.models.Corpus;
import com.tetkole.tetkole.utils.models.FieldAudio;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnnotationsVisualization extends Pane {
    private static final int annotationSize = 15;
    private List<Annotation> annotations = new ArrayList<>();
    private final List<Rectangle> annotationsRectangles = new ArrayList<>();
    private HBox actualAnnotationMenu = null;
    private double ratioAudio;
    private double beginAudio = 0;
    private double endAudio = 0;
    private FieldAudio fieldAudio;
    private VBox vBoxPane;
    private List<HBox> lines = new ArrayList<>();
    private RecordManager recordManager;
    private String corpusName;

    public AnnotationsVisualization() {

        this.widthProperty().addListener((observable , oldValue , newValue) -> {
            this.getChildren().remove(actualAnnotationMenu);
            actualAnnotationMenu = null;
            this.drawAnnotations();
        });
    }

    public void drawAnnotations() {

        this.getChildren().removeAll(annotationsRectangles);

        for( Annotation annotation : annotations) {
            double annotationStart = annotation.getStart();
            double annotationEnd = annotation.getEnd();
            if(!(annotationEnd < this.beginAudio || annotationStart > this.endAudio)){
                Rectangle r = initRectangle(
                        ratioAudio * (annotationStart - this.beginAudio),
                        this.getHeight()/2,
                        ratioAudio * (annotationEnd - this.beginAudio) - ratioAudio * (annotationStart - this.beginAudio),
                        annotation
                        );

                annotationsRectangles.add(r);
                this.getChildren().add(r);
            }
        }
    }

    public void setValueFromWave(double ratioAudio, double beginAudio , double endAudio) {
        this.ratioAudio = ratioAudio;
        this.beginAudio = beginAudio;
        this.endAudio = endAudio;
    }

    private Rectangle initRectangle(double X, double Y, double W, Annotation annotation) {

        Rectangle r = new Rectangle(X,Y,W, AnnotationsVisualization.annotationSize);
        r.setFill(Color.ORANGE);
        r.setArcWidth(10);
        r.setArcHeight(10);
        r.setStroke(Color.WHITE);

        Button btnPlayPause = new Button("PlayPause");
        Button btnRecord = new Button("ReRecord");
        Button btnDelete = new Button("Delete");
        Button btnClose = new Button("X");

        btnPlayPause.setOnAction(event -> annotation.playPause());

        btnRecord.setOnAction(event -> handleRecord(annotation));

        btnDelete.setOnAction(event -> {
            this.vBoxPane.getChildren().remove(lines.get(annotations.indexOf(annotation)));
            this.delete(annotation);
        });

        btnClose.setOnAction(event -> this.closePopup());

        HBox hbox = new HBox(btnPlayPause, btnRecord, btnDelete, btnClose);
        hbox.setSpacing(3);
        hbox.setPadding(new Insets(3));
        hbox.setStyle(
                "-fx-background-color: #FFFFFF;"
                        +"-fx-border-style: solid inside;"
                        +"-fx-border-width: 2;"
                        +"-fx-border-color: black;"
        );

        r.setOnMousePressed(event -> {
            if(this.getChildren().contains(hbox)){
                this.getChildren().remove(hbox);
                actualAnnotationMenu = null;
            }else{
                if(actualAnnotationMenu != null){
                    this.getChildren().remove(actualAnnotationMenu);
                }
                this.getChildren().add(hbox);
                actualAnnotationMenu = hbox;
                hbox.setTranslateX(r.getX());
                hbox.setTranslateY(0.0);
            }
        });

        return r;
    }

    public void setFieldAudio(FieldAudio fieldAudio) {
        this.fieldAudio = fieldAudio;
        this.annotations = this.fieldAudio.getAnnotations();
    }

    public void closePopup(){
        if(actualAnnotationMenu != null){
            getChildren().remove(actualAnnotationMenu);
            actualAnnotationMenu = null;
        }
    }

    public void delete(Annotation annotation) {
        this.fieldAudio.deleteAnnotation(annotation);
        this.annotations.remove(annotation);
        this.refresh();
    }

    public void handleRecord(Annotation annotation) {
        if (!recordManager.isRecording()) {

            // get the date for the record name
            String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("(dd-MM-yyyy_HH'h'mm'm'ss's'_SSS)"));
            formattedDateTime = formattedDateTime.substring(1, formattedDateTime.length()-1);
            String recordName = "newAnnotation_" + formattedDateTime + ".wav";

            String corpusPath = "/" + this.corpusName + "/" + Corpus.folderNameAnnotation + "/" + this.fieldAudio.getName();
            this.recordManager.startRecording(this.fieldAudio.getName(), recordName, corpusPath, annotation.getEnd(), annotation.getStart());
        }
        else
        {
            this.recordManager.stopRecording(this.fieldAudio);
            this.vBoxPane.getChildren().remove(lines.get(annotations.indexOf(annotation)));
            this.lines.remove(lines.get(annotations.indexOf(annotation)));
            this.delete(annotation);

            //TODO add line in AudioEditSceneController lines array
        }
    }

    public void setvBoxPane(VBox vBoxPane) {
        this.vBoxPane = vBoxPane;
    }

    public List<HBox> getLines() {
        return lines;
    }

    public void setCorpusName(String corpusName) {
        this.corpusName = corpusName;
    }

    public void setRecordManager(RecordManager recordManager) {
        this.recordManager = recordManager;
    }

    public void refresh() {
        this.drawAnnotations();
        this.closePopup();
    }

    public void addLine(HBox e) {
        this.lines.add(e);
    }
}

