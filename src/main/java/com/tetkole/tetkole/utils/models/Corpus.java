package com.tetkole.tetkole.utils.models;

import com.tetkole.tetkole.utils.FileManager;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Corpus {

    /* STATIC SECTION */

    public static final String folderNameFieldAudio = "FieldAudio";
    public static final String folderNameCorpusImage = "Images";
    public static final String folderNameCorpusVideo = "Videos";
    public static final String folderNameAnnotation = "Annotations";

    /**
     * ONLY USE this method to create a new corpus
     */
    public static Corpus createCorpus(String name) {
        FileManager.getFileManager().createFolder(name);
        FileManager.getFileManager().createFolder(name, folderNameFieldAudio);
        FileManager.getFileManager().createFolder(name, folderNameCorpusImage);
        FileManager.getFileManager().createFolder(name, folderNameCorpusVideo);
        FileManager.getFileManager().createFolder(name, folderNameAnnotation);

        return new Corpus(name);
    }


    /**
     * Return a list of every corpus on the Tetkole folder.
     */
    public static List<Corpus> getAllCorpus() {
        List<Corpus> corpusList = new ArrayList<>();

        // Loop on every file in Tetkole Fodler
        for (File corpusFolder : Objects.requireNonNull(new File(FileManager.getFileManager().getFolderPath()).listFiles())) {

            Corpus corpus = new Corpus(corpusFolder.getName());

            // Loop on every file in a Corpus Folder
            for (File folder : Objects.requireNonNull(corpusFolder.listFiles())) {

                // Manage FieldAudio folder and his content
                if (folder.getName().equals(folderNameFieldAudio)) {
                    for (File file : Objects.requireNonNull(folder.listFiles())) {
                        corpus.fieldAudios.add(new FieldAudio(file));
                    }
                }

            }

            corpusList.add(corpus);
        }


        return corpusList;
    }


    /* NON STATIC SECTION */

    private String name;
    private List<FieldAudio> fieldAudios;
    private List<CorpusImage> corpusImages;
    private List<CorpusVideo> corpusVideos;


    public Corpus(String name) {
        this.name = name;
        this.fieldAudios = new ArrayList<>();
        this.corpusImages = new ArrayList<>();
        this.corpusVideos = new ArrayList<>();
    }


    /**
     * ONLY USE this method to create/add a new FieldAudio in a corpus
     */
    public void createFieldAudio() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav")
        );

        // wait for user to choose an audio
        File file = fileChooser.showOpenDialog(null);

        if (file == null) {
            Label audioErrorLabel = new Label("Ce fichier ne peut pas être chargé");
            Alert alert = new Alert(Alert.AlertType.ERROR, audioErrorLabel.getText());
            alert.showAndWait();
        } else {
            FieldAudio fa = new FieldAudio(FileManager.getFileManager().copyFile(file, this.name + "/" + folderNameFieldAudio));
            FileManager.getFileManager().createFolder("/" + this.name + "/" + folderNameAnnotation, fa.getName());
            this.fieldAudios.add(fa);
        }
    }


    public String getName() {
        return this.name;
    }


    public List<FieldAudio> getFieldAudios() {
        return this.fieldAudios;
    }

    public List<CorpusImage> getCorpusImages() {
        return this.corpusImages;
    }

    public List<CorpusVideo> getCorpusVideos() {
        return this.corpusVideos;
    }
}
