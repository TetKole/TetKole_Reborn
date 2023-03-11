package com.tetkole.tetkole.utils.models;

import com.tetkole.tetkole.utils.FileManager;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

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
            case PLAYING -> mediaPlayer.stop();
            case PAUSED, READY, STOPPED -> mediaPlayer.play();
        }
    }

    public double getStart() {
        return start;
    }

    public double getEnd() {
        return end;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public File getJsonFile() {
        String name = this.file.getName();
        return new File(this.file.getParentFile() + "/" + name.substring(0, name.length() - 3) + "json");
    }

    public void renameAnnotation(String newName) {
        this.mediaPlayer.dispose();
        File jsonFile = this.getJsonFile();
        JSONObject jsonObject =  FileManager.getFileManager().readJSONFile(jsonFile);
        jsonObject.put("recordName", newName);
        File lastFile = this.file;
        this.file = null;
        FileManager.getFileManager().writeJSONFile(jsonFile, jsonObject);
        FileManager.getFileManager().renameFile(jsonFile, newName.split("\\.")[0] + ".json");
        FileManager.getFileManager().renameFile(lastFile, newName);
        FileManager.getFileManager().renameDirectoryAnnotation(lastFile.getName(), corpusName, fieldAudioName, newName);
        this.file = FileManager.getFileManager().getAnnotationFile(newName, corpusName, fieldAudioName);
    }

    public void renameDocName(String newDocName) {
        File jsonFile = this.getJsonFile();
        JSONObject jsonObject =  FileManager.getFileManager().readJSONFile(jsonFile);
        jsonObject.put("fileName", newDocName);
        FileManager.getFileManager().writeJSONFile(jsonFile, jsonObject);
        this.fieldAudioName = newDocName;
    }

    public int getId() {
        File file = new File(FileManager.getFileManager().getFolderPath() + "/" + corpusName + "/corpus_state.json");
        if (!file.exists()) {
            System.out.println("corpus state doesn't exist on your computer");
            return -1;
        }
        JSONObject corpus_state = FileManager.getFileManager().readJSONFile(file);
        JSONArray documents = corpus_state.getJSONArray("documents");

        File jsonFile = this.getJsonFile();
        JSONObject jsonObject =  FileManager.getFileManager().readJSONFile(jsonFile);
        String recordName = jsonObject.getString("recordName");

        for (int i=0; i< documents.length(); i++) {
            JSONObject document = documents.getJSONObject(i);
            if (document.getString("name").equals(this.fieldAudioName)) {
                JSONArray annotations = document.getJSONArray("annotations");
                for (int j=0; j< annotations.length(); j++) {
                    JSONObject annotation = annotations.getJSONObject(j);
                    if(annotation.get("name").equals(recordName)) {
                        return annotation.getInt("annotationId");
                    }
                }
            }
        }

        return -1;
    }
}
