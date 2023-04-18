package com.tetkole.tetkole.utils.annotations;

import com.tetkole.tetkole.components.CustomButton;
import com.tetkole.tetkole.controllers.AudioEditSceneController;
import com.tetkole.tetkole.utils.models.Annotation;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnnotationsVisualization extends Pane {

    public static final int MAX_NUMBER_OF_TIRES = 5;

    // Change it to modify annotationsRectangles height
    private static double annotationSize = 15;
    private final List<Rectangle> annotationsRectangles = new ArrayList<>();
    private final List<HBox> annotationsRectanglesMenu = new ArrayList<>();
    private VBox actualAnnotationMenu = null;
    private double ratioAudio;
    private double beginAudio = 0;
    private double endAudio = 0;
    private AudioEditSceneController audioEditSceneController;

    public AnnotationsVisualization() {

        this.widthProperty().addListener((observable , oldValue , newValue) -> {
            this.getChildren().remove(actualAnnotationMenu);
            actualAnnotationMenu = null;
            this.drawAnnotations();
        });

        this.heightProperty().addListener((observable , oldValue , newValue) -> {
            this.getChildren().remove(actualAnnotationMenu);
            actualAnnotationMenu = null;
            this.drawAnnotations();
            annotationSize = (newValue.doubleValue() / MAX_NUMBER_OF_TIRES) / 2;
        });
    }

    public void drawAnnotations() {

        this.getChildren().removeAll(annotationsRectangles);
        annotationsRectangles.clear();
        annotationsRectanglesMenu.clear();

        int i = 0;
        for (Annotation annotation : this.audioEditSceneController.getFieldAudio().getAnnotations()) {
            double annotationStart = annotation.getStart();
            double annotationEnd = annotation.getEnd();

            if (this.audioEditSceneController.getLines().size() != 0){
                Rectangle r;
                if (!(annotationEnd < this.beginAudio || annotationStart > this.endAudio)) {

                    r = initRectangle(
                            ratioAudio * (annotationStart - this.beginAudio),
                            (this.getHeight() / MAX_NUMBER_OF_TIRES) * annotation.getTire(),
                            ratioAudio * (annotationEnd - this.beginAudio) - ratioAudio * (annotationStart - this.beginAudio),
                            this.audioEditSceneController.getLines().get(i),
                            annotation
                    );

                }else{

                    r = initRectangle(
                            0, 0, 0,
                            this.audioEditSceneController.getLines().get(i),
                            annotation
                    );

                }
                annotationsRectangles.add(r);
                this.getChildren().add(r);
            }

            i++;
        }
    }

    public void setValueFromWave(double ratioAudio, double beginAudio , double endAudio) {
        this.ratioAudio = ratioAudio;
        this.beginAudio = beginAudio;
        this.endAudio = endAudio;
    }

    private Rectangle initRectangle(double X, double Y, double W, HBox line, Annotation annotation) {

        Rectangle r = new Rectangle(X,Y,W, AnnotationsVisualization.annotationSize);
        r.setFill(Color.ORANGE);
        r.setArcWidth(10);
        r.setArcHeight(10);
        r.setStroke(Color.WHITE);

        CustomButton btnPlayPause = new CustomButton(Objects.requireNonNull(getClass().getResource("/images/play.png")).toExternalForm());
        CustomButton btnRecord = new CustomButton(Objects.requireNonNull(getClass().getResource("/images/reRecord.png")).toExternalForm());
        CustomButton btnDelete = new CustomButton(Objects.requireNonNull(getClass().getResource("/images/trash.png")).toExternalForm());
        CustomButton btnEdit = new CustomButton(Objects.requireNonNull(getClass().getResource("/images/edit.png")).toExternalForm());
        Button btnClose = new Button("X");

        btnPlayPause.resizeImage(10);
        btnRecord.resizeImage(10);
        btnDelete.resizeImage(10);
        btnEdit.resizeImage(10);

        btnPlayPause.setOnAction(((Button)line.getChildren().get(4)).getOnAction());
        btnRecord.setOnAction(((Button)line.getChildren().get(1)).getOnAction());
        btnEdit.setOnAction(((Button)line.getChildren().get(3)).getOnAction());
        btnDelete.setOnAction(e -> {
            this.audioEditSceneController.getLines().remove(line);
            this.audioEditSceneController.getvBoxPane().getChildren().remove(line);
            this.audioEditSceneController.getFieldAudio().deleteAnnotation(annotation);
            this.refresh();
        });

        btnClose.setOnAction(event -> this.closePopup());

        HBox hbox = new HBox(btnPlayPause, btnRecord, btnDelete, btnEdit, btnClose);
        hbox.setSpacing(3);
        hbox.setPadding(new Insets(3));
        hbox.setAlignment(Pos.CENTER);

        Label annotationLabelName = new Label(annotation.getName());
        annotationLabelName.setMaxWidth(this.getWidth());
        annotationLabelName.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(hbox, annotationLabelName);
        vbox.setPadding(new Insets(3));
        vbox.setStyle(
                "-fx-background-color: #FFFFFF;"
                        +"-fx-border-style: solid inside;"
                        +"-fx-border-width: 2;"
                        +"-fx-border-color: #5fb8ff;"
                        +"-fx-border-radius: 10;"
                        +"-fx-background-radius: 10;"
        );

        ChangeListener listenerWidth = (observable, oldValue, newValue) -> {
            this.getChildren().remove(vbox);
            double x_value = r.getX();
            vbox.setTranslateX(x_value + (double)newValue >= this.getWidth() ? this.getWidth() - (double)newValue : x_value );
            this.getChildren().add(vbox);
        };

        ChangeListener listenerHeight = (observable, oldValue, newValue) -> {
            this.getChildren().remove(vbox);
            double y_value = r.getY() - vbox.getHeight();
            vbox.setTranslateY(y_value > r.getHeight() ? y_value : annotationSize);
            this.getChildren().add(vbox);
        };

        vbox.widthProperty().addListener(listenerWidth);
        vbox.heightProperty().addListener(listenerHeight);

        r.setOnMousePressed(event -> {
            /*Go to annotation
            if(event.getClickCount() == 2){
                audioEditSceneController.goToAnnotation(annotation.getStart(), annotation.getEnd());
            }*/
            if(this.getChildren().contains(vbox)){
                this.getChildren().remove(vbox);
                actualAnnotationMenu = null;
            }else{
                if(actualAnnotationMenu != null){
                    this.getChildren().remove(actualAnnotationMenu);
                }
                this.getChildren().add(vbox);
                actualAnnotationMenu = vbox;
                double x_value = r.getX();
                double right_x_value = vbox.getWidth() == 0 ? 206.0 : vbox.getWidth();
                vbox.setTranslateX(x_value + right_x_value >= this.getWidth() ? this.getWidth() - right_x_value : x_value );
                double y_value = r.getY() - vbox.getHeight();
                vbox.setTranslateY(y_value >= r.getHeight() ? y_value : annotationSize);
            }
        });

        annotationsRectanglesMenu.add(hbox);
        return r;
    }

    public void closePopup(){
        if(actualAnnotationMenu != null){
            getChildren().remove(actualAnnotationMenu);
            actualAnnotationMenu = null;
        }
    }

    public void refresh() {
        this.closePopup();
        this.drawAnnotations();
    }

    public void setEditSceneController(AudioEditSceneController audioEditSceneController) {
        this.audioEditSceneController = audioEditSceneController;
    }

    public List<HBox> getAnnotationsRectanglesMenu() {
        return annotationsRectanglesMenu;
    }
}

