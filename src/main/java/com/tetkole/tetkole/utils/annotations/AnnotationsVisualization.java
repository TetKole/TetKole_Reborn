package com.tetkole.tetkole.utils.annotations;

import com.tetkole.tetkole.utils.models.Annotation;
import com.tetkole.tetkole.utils.wave.WaveFormService;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class AnnotationsVisualization extends Canvas {

    private final GraphicsContext gc = getGraphicsContext2D();
    private static final int annotationSize = 15;
    private List<Annotation> annotations = new ArrayList<>();
    private double ratioAudio;
    private double beginAudio = 0;
    private double endAudio = 0;

    public AnnotationsVisualization() {
        this.setHeight(200);
        this.setWidth(500);
        gc.setFill(Color.web("#155823"));
        gc.fillRect(0, 0, getWidth(), getHeight());

        widthProperty().addListener((observable , oldValue , newValue) -> {
            this.setWidth((double)newValue);
            this.drawAnnotations();
        });
    }

    public void drawAnnotations() {
        gc.setStroke(Color.CHARTREUSE);
        gc.setFont(new Font(annotationSize));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFill(Color.web("#252525"));
        gc.fillRect(0, 0, getWidth(), getHeight());

        int i = 0;
        for( Annotation annotation : annotations) {
            double annotationStart = annotation.getStart();
            double annotationEnd = annotation.getEnd();
            if(!(annotationEnd < this.beginAudio || annotationStart > this.endAudio)){
                gc.setFill(Color.CHARTREUSE);
                gc.fillRect(ratioAudio * (annotationStart - this.beginAudio), this.getHeight()/2 - annotationSize/2,
                        ratioAudio * (annotationEnd - this.beginAudio) - ratioAudio * (annotationStart - this.beginAudio), annotationSize);
                gc.setFill(Color.BLACK);
                gc.fillText(String.valueOf(i), ratioAudio * (annotationStart - this.beginAudio) + annotationSize/2, this.getHeight()/2);
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
    @Override
    public boolean isResizable() {
        return true;
    }
    @Override
    public double prefWidth(double height) {
        return getWidth();
    }
    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

}

