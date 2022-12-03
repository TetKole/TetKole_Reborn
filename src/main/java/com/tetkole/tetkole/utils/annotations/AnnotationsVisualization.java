package com.tetkole.tetkole.utils.annotations;

import com.tetkole.tetkole.utils.models.Annotation;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import java.util.List;

public class AnnotationsVisualization extends Canvas {

    public final GraphicsContext gc = getGraphicsContext2D();
    static final int annotationSize = 15;
    public List<Annotation> annotations;
    double ratioAudio;

    public AnnotationsVisualization() {
        this.setHeight(500);
        this.setWidth(500);
        gc.setFill(Color.web("#155823"));
        gc.fillRect(0, 0, getWidth(), getHeight());

        widthProperty().addListener((observable , oldValue , newValue) -> {
            gc.clearRect(0, 0, getWidth(), getHeight());
        });
    }

    public void drawAnnotations() {
        gc.setStroke(Color.CHARTREUSE);
        gc.setFont(new Font(annotationSize));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        int i = 0;
        for( Annotation annotation : annotations) {

            double annotationStart = annotation.getStart();
            double annotationEnd = annotation.getEnd();
            gc.strokeLine(ratioAudio * annotationStart, getHeight()/2, ratioAudio * annotationEnd, getHeight()/2);
            gc.setFill(Color.CHARTREUSE);
            gc.fillRect(ratioAudio * annotationStart, this.getHeight()/2 - annotationSize/2, annotationSize, annotationSize);
            gc.fillRect(ratioAudio * annotationEnd, this.getHeight()/2 - annotationSize/2, annotationSize, annotationSize);
            gc.setFill(Color.BLACK);
            gc.fillText(String.valueOf(i), ratioAudio * annotationStart + annotationSize/2, this.getHeight()/2);
            gc.fillText(String.valueOf(i), ratioAudio * annotationEnd + annotationSize/2, this.getHeight()/2);

            i++;
        }
    }

    public void setRatioAudio(double ratioAudio) {
        this.ratioAudio = ratioAudio;
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

