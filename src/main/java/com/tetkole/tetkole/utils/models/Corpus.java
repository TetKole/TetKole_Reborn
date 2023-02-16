package com.tetkole.tetkole.utils.models;

import com.tetkole.tetkole.utils.FileManager;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import org.json.JSONObject;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public static void createCorpus(String name) {
        FileManager fileManager = FileManager.getFileManager();
        fileManager.createFolder(name);
        fileManager.createFolder(name, folderNameFieldAudio);
        fileManager.createFolder(name, folderNameCorpusImage);
        fileManager.createFolder(name, folderNameCorpusVideo);
        fileManager.createFolder(name, folderNameAnnotation);
        fileManager.createCorpusModifFile(name);
    }


    /**
     * Return a list of every corpus on the Tetkole folder.
     */
    public static List<Corpus> getAllCorpus() {
        List<Corpus> corpusList = new ArrayList<>();

        // Loop on every file in Tetkole Fodler, which means on every Corpus
        for (File corpusFolder : Objects.requireNonNull(new File(FileManager.getFileManager().getFolderPath()).listFiles())) {

            Corpus corpus = new Corpus(corpusFolder.getName());

            // get the corpus_modif.json
            corpus.corpus_modif = FileManager.getFileManager().readJSONFile(
                    new File(FileManager.getFileManager().getFolderPath() + "/" + corpus.getName() + "/corpus_modif.json")
            );

            /* Manage fieldAudios in FieldAudios Folder */
            File fieldAudiosFolder = new File(FileManager.getFileManager().getFolderPath() + "/" + corpus.getName() + "/" + folderNameFieldAudio);
            for (File audioFile : Objects.requireNonNull(fieldAudiosFolder.listFiles(file -> !file.getName().endsWith("json")))) {

                File jsonFile = Objects.requireNonNull(fieldAudiosFolder.listFiles(file ->
                        file.getName().endsWith("json")
                ))[0];

                JSONObject jsonObject = FileManager.getFileManager().readJSONFile(jsonFile);

                corpus.fieldAudios.add(new FieldAudio(audioFile, jsonObject.getString("description"), corpus));
            }

            /* Manage videos in Videos Folder */
            File videosFolder = new File(FileManager.getFileManager().getFolderPath() + "/" + corpus.getName() + "/" + folderNameCorpusVideo);
            for (File file : Objects.requireNonNull(videosFolder.listFiles())) {
                corpus.corpusVideos.add(new CorpusVideo(file, corpus));
            }

            /* Manage images in Images Folder */
            File imagesFolder = new File(FileManager.getFileManager().getFolderPath() + "/" + corpus.getName() + "/" + folderNameCorpusImage);
            for (File file : Objects.requireNonNull(imagesFolder.listFiles())) {
                corpus.corpusImages.add(new CorpusImage(file, corpus));
            }


            /* Manage annotations in Annotations Folder */
            File annotationsFolder = new File(FileManager.getFileManager().getFolderPath() + "/" + corpus.getName() + "/" + folderNameAnnotation);

            for (File folderFieldAudio : Objects.requireNonNull(annotationsFolder.listFiles())) {
                // we get the field audio corresponding to the folder
                Media media = corpus.getMediaByName(folderFieldAudio.getName());

                // Loop on every annotation in the folder
                for (File annotationFolder : Objects.requireNonNull(folderFieldAudio.listFiles())) {

                    // Search for json and wave files for each annotation
                    File jsonFile = null;
                    File audioFile = null;
                    // this for only loop twice, we can't predict file names here, so we need a for :(
                    for (File file : Objects.requireNonNull(annotationFolder.listFiles())) {
                        if (file.getName().endsWith(".json")) {
                            jsonFile = file;
                        }
                        if (file.getName().endsWith(".wav")) {
                            audioFile = file;
                        }
                    }

                    // Read JSON
                    JSONObject jsonObject = FileManager.getFileManager().readJSONFile(Objects.requireNonNull(jsonFile));
                    double start = jsonObject.getDouble("start");
                    double end = jsonObject.getDouble("end");

                    // Create annotation
                    Objects.requireNonNull(media).addAnnotation(new Annotation(audioFile, start, end, folderFieldAudio.getName(), corpus.getName()));

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

    private JSONObject corpus_modif;


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
        File fileAudio = fileChooser.showOpenDialog(null);

        if (fileAudio != null) {
            // copy of the file in corpus folder
            File file = FileManager.getFileManager().copyFile(fileAudio, this.name + "/" + folderNameFieldAudio);

            // rename file to remove acccent
            String newName = Normalizer.normalize(file.getName(), Normalizer.Form.NFD);
            newName = newName.replaceAll("\\p{InCombiningDiacriticalMarks}", "");
            file = FileManager.getFileManager().renameFile(file, newName);

            // creates corresponding JSON File
            FileManager.getFileManager().createJSONFile(newName, Map.of("description", ""), String.format("/%s/FieldAudio", this.getName()));

            // create new FieldAudio
            FieldAudio fieldAudio = new FieldAudio(file, this);

            // create annotation's folder for this media
            FileManager.getFileManager().createFolder("/" + this.name + "/" + folderNameAnnotation, fieldAudio.getName());

            this.fieldAudios.add(fieldAudio);

            // corpus_modif
            JSONObject modif = new JSONObject();
            modif.put("type", Corpus.folderNameFieldAudio);
            modif.put("name", fieldAudio.getName());
            this.corpus_modif.getJSONObject("added").getJSONArray("documents").put(modif);
            this.writeCorpusModif();
        } else {
            Label audioErrorLabel = new Label("Ce fichier ne peut pas être chargé");
            Alert alert = new Alert(Alert.AlertType.ERROR, audioErrorLabel.getText());
            alert.showAndWait();
        }
    }

    public void createVideo() {
        FileChooser fileChooserVideo = new FileChooser();
        fileChooserVideo.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("VideoFile", "*.mp4")
        );

        File fileVideo = fileChooserVideo.showOpenDialog(null);
        if (fileVideo != null) {
            // copy of the file in corpus folder
            File file = FileManager.getFileManager().copyFile(fileVideo, this.name + "/" + folderNameCorpusVideo);

            // rename file to remove acccent
            String newName = Normalizer.normalize(fileVideo.getName(), Normalizer.Form.NFD);
            newName = newName.replaceAll("\\p{InCombiningDiacriticalMarks}", "");
            file = FileManager.getFileManager().renameFile(file, newName);

            // create new CorpusVideos
            CorpusVideo video = new CorpusVideo(file, this);

            // create annotation's folder for this media
            FileManager.getFileManager().createFolder("/" + this.name + "/" + folderNameAnnotation, video.getName());

            this.corpusVideos.add(video);

            // corpus_modif
            JSONObject modif = new JSONObject();
            modif.put("type", Corpus.folderNameCorpusVideo);
            modif.put("name", video.getName());
            this.corpus_modif.getJSONObject("added").getJSONArray("documents").put(modif);
            this.writeCorpusModif();
        } else {
            Label audioErrorLabel = new Label("Ce fichier ne peut pas être chargé");
            Alert alert = new Alert(Alert.AlertType.ERROR, audioErrorLabel.getText());
            alert.showAndWait();
        }
    }

    public void createImage() {
        FileChooser fileChooserImage = new FileChooser();
        fileChooserImage.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("ImageFile", "*.jpg", "*.jpeg", "*.png")
        );

        File fileImage = fileChooserImage.showOpenDialog(null);

        if (fileImage != null){
            // copy of the file in corpus folder
            File file = FileManager.getFileManager().copyFile(fileImage, this.name + "/" + folderNameCorpusImage);

            // rename file to remove acccent
            String newName = Normalizer.normalize(fileImage.getName(), Normalizer.Form.NFD);
            newName = newName.replaceAll("\\p{InCombiningDiacriticalMarks}", "");
            file = FileManager.getFileManager().renameFile(file, newName);

            // create new CorpusImage
            CorpusImage image = new CorpusImage(file, this);

            // create annotation's folder for this media
            FileManager.getFileManager().createFolder("/" + this.name + "/" + folderNameAnnotation, image.getName());

            this.corpusImages.add(image);

            // corpus_modif
            JSONObject modif = new JSONObject();
            modif.put("type", Corpus.folderNameCorpusImage);
            modif.put("name", image.getName());
            this.corpus_modif.getJSONObject("added").getJSONArray("documents").put(modif);
            this.writeCorpusModif();
        } else {
            Label audioErrorLabel = new Label("Ce fichier ne peut pas être chargé");
            Alert alert = new Alert(Alert.AlertType.ERROR, audioErrorLabel.getText());
            alert.showAndWait();
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

    public Media getMediaByName(String name) {
        for (FieldAudio fa : this.fieldAudios) {
            if (fa.getName().equals(name)) {
                return fa;
            }
        }
        for (CorpusVideo video : this.corpusVideos) {
            if (video.getName().equals(name)) {
                return video;
            }
        }
        for (CorpusImage image : this.corpusImages) {
            if (image.getName().equals(name)) {
                return image;
            }
        }
        return null;
    }

    public void writeCorpusModif() {
        System.out.println(this.corpus_modif);
        File file = new File(FileManager.getFileManager().getFolderPath() + "/" + name + "/corpus_modif.json");
        FileManager.getFileManager().writeJSONFile(file, corpus_modif);
    }

    public JSONObject getCorpusModif() {
        return this.corpus_modif;
    }
}
