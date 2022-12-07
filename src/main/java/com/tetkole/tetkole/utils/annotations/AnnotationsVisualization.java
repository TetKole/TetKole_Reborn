package com.tetkole.tetkole.utils.annotations;

import com.tetkole.tetkole.utils.models.Annotation;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class AnnotationsVisualization extends Pane {
    private static final int annotationSize = 15;
    private List<Annotation> annotations = new ArrayList<>();
    private final List<Rectangle> annotationsRectangles = new ArrayList<>();
    private double ratioAudio;
    private double beginAudio = 0;
    private double endAudio = 0;

    public AnnotationsVisualization() {

        this.widthProperty().addListener((observable , oldValue , newValue) -> {
            this.drawAnnotations();
        });
    }

    public void drawAnnotations() {

        int i = 0;
        this.getChildren().removeAll(annotationsRectangles);
        for( Annotation annotation : annotations) {
            double annotationStart = annotation.getStart();
            double annotationEnd = annotation.getEnd();
            if(!(annotationEnd < this.beginAudio || annotationStart > this.endAudio)){
                Rectangle r = new Rectangle();
                r.setFill(Color.ORANGE);
                r.setX(ratioAudio * (annotationStart - this.beginAudio));
                r.setY(this.getHeight()/2 - annotationSize/2);
                r.setHeight(annotationSize);
                r.setWidth(ratioAudio * (annotationEnd - this.beginAudio) - ratioAudio * (annotationStart - this.beginAudio));
                r.setArcWidth(10);
                r.setArcHeight(10);
                r.setStroke(Color.WHITE);
                int finalI = i;

                r.setOnMousePressed(event -> {
                    System.out.println("Annotations : " + finalI);
                });

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
    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }
}

