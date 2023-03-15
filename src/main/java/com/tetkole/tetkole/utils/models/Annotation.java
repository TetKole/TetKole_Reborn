package com.tetkole.tetkole.utils.models;

import com.tetkole.tetkole.utils.FileManager;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
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
        this.mediaPlayer = null;

        File jsonFile = this.getJsonFile();
        JSONObject jsonObject =  FileManager.getFileManager().readJSONFile(jsonFile);
        jsonObject.put("recordName", newName);
        FileManager.getFileManager().writeJSONFile(jsonFile, jsonObject);

        FileManager.getFileManager().renameFile(jsonFile, newName.split("\\.")[0] + ".json");

        String lastName = this.file.getName();
        FileManager.getFileManager().renameFile(this.file, newName);
        FileManager.getFileManager().renameDirectoryAnnotation(lastName, corpusName, fieldAudioName, newName);
        this.file = FileManager.getFileManager().getAnnotationFile(newName, corpusName, fieldAudioName);
        this.mediaPlayer = new MediaPlayer(new Media(file.toURI().toString()));
    }

    public void renameDocName(String newDocName) {
        File jsonFile = this.getJsonFile();
        JSONObject jsonObject =  FileManager.getFileManager().readJSONFile(jsonFile);
        jsonObject.put("fileName", newDocName);
        FileManager.getFileManager().writeJSONFile(jsonFile, jsonObject);
        this.fieldAudioName = newDocName;
        this.file = FileManager.getFileManager().getAnnotationFile(this.getName(), this.corpusName, this.fieldAudioName);
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

        int docId = -1;

        File fileModif = new File(FileManager.getFileManager().getFolderPath() + "/" + corpusName + "/corpus_modif.json");
        if (file.exists()) {
            JSONObject corpus_modif = FileManager.getFileManager().readJSONFile(fileModif);
            JSONArray documentsUpdated = corpus_modif.getJSONObject("updated").getJSONArray("documents");
            for (int i = 0; i < documentsUpdated.length(); i++) {
                JSONObject document = documentsUpdated.getJSONObject(i);
                System.out.println(documentsUpdated);
                if(document.getString("newName").equals(this.fieldAudioName)) {
                    docId = document.getInt("id");
                    break;
                }
            }
        }

        for (int i=0; i< documents.length(); i++) {
            JSONObject document = documents.getJSONObject(i);
            if (document.getString("name").equals(this.fieldAudioName) || document.getInt("docId") == docId) {
                JSONArray annotations = document.getJSONArray("annotations");
                for (int j=0; j< annotations.length(); j++) {
                    JSONObject annotation = annotations.getJSONObject(j);
                    System.out.println("Record :" + recordName + " JSON : " + annotation.get("name"));
                    if(annotation.get("name").equals(recordName)) {
                        return annotation.getInt("annotationId");
                    }
                }
            }
        }

        return -1;
    }

    public String getFieldAudioName() {
        return fieldAudioName;
    }
}
