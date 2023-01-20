package com.tetkole.tetkole.utils.models;

import com.tetkole.tetkole.utils.FileManager;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.Objects;

public class Annotation {
    private File file;

    private MediaPlayer mediaPlayer;
    private final double start;
    private final double end;
    private String fieldAudioName;
    private String corpusName;

    public Annotation(File file, double start, double end, String fieldAudioName, String corpusName) {
        this.file = file;
        this.start = start;
        this.end = end;
        this.fieldAudioName = fieldAudioName;
        this.corpusName = corpusName;
        this.mediaPlayer = new MediaPlayer(new Media(file.toURI().toString()));
    }

    public String getName() {
        return this.file.getName();
    }

    public File getFile() {
        return this.file;
    }

    public void delete() {
        this.mediaPlayer.dispose();
        String annotationDirPath = FileManager.getFileManager().getFolderPath() + "/" + this.corpusName + "/" + Corpus.folderNameAnnotation + "/" + this.fieldAudioName + "/" + this.getName();
        FileManager.getFileManager().deleteFolder(new File(annotationDirPath));
    }

    public void playPause() {
        switch (mediaPlayer.getStatus()) {
            case PLAYING -> {
                mediaPlayer.stop();
                //btnPlayPause.setText(resources.getString("Play"));
                //((ImageView) btnPlayPause.getGraphic()).setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/play.png")).toExternalForm()));
            }
            case PAUSED, READY, STOPPED -> {
                mediaPlayer.play();
                //btnPlayPause.setText(resources.getString("Pause"));
                //((ImageView) btnPlayPause.getGraphic()).setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/pause.png")).toExternalForm()));
            }
        }
    }

    public double getStart() {
        return start;
    }

    public double getEnd() {
        return end;
    }

    public String getFieldAudioName() {
        return fieldAudioName;
    }

    public String getCorpusName() {
        return corpusName;
    }
}
