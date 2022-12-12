package com.tetkole.tetkole.utils.annotations;

import com.tetkole.tetkole.utils.SceneManager;
import com.tetkole.tetkole.utils.models.Annotation;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.*;
import javafx.stage.Window;
import java.util.ArrayList;
import java.util.List;

public class AnnotationsVisualization extends Pane {
    private static final int annotationSize = 15;
    private List<Annotation> annotations = new ArrayList<>();
    private final List<Rectangle> annotationsRectangles = new ArrayList<>();
    private HBox actualAnnotationMenu = null;
    private double ratioAudio;
    private double beginAudio = 0;
    private double endAudio = 0;

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

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    private Rectangle initRectangle(double X, double Y, double W, Annotation annotation) {

        Rectangle r = new Rectangle(X,Y,W, AnnotationsVisualization.annotationSize);
        r.setFill(Color.ORANGE);
        r.setArcWidth(10);
        r.setArcHeight(10);
        r.setStroke(Color.WHITE);

        Button btnPlayPause = new Button("PlayPause");
        Button btnDelete = new Button("Delete");
        Button btnClose = new Button("X");

        btnPlayPause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("lire");
            }
        });

        btnDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("delete");
            }
        });

        btnClose.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(actualAnnotationMenu != null){
                    getChildren().remove(actualAnnotationMenu);
                    actualAnnotationMenu = null;
                }
            }
        });

        HBox hbox = new HBox(btnPlayPause,btnDelete,btnClose);
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
}

