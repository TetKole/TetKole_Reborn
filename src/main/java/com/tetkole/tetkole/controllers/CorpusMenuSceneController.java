package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.SceneManager;
import com.tetkole.tetkole.utils.models.Corpus;
import com.tetkole.tetkole.utils.models.CorpusImage;
import com.tetkole.tetkole.utils.models.CorpusVideo;
import com.tetkole.tetkole.utils.models.FieldAudio;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class CorpusMenuSceneController implements Initializable {

    @FXML
    private HBox header;

    @FXML
    private VBox vBoxFieldAudios;
    @FXML
    private VBox vBoxImages;
    @FXML
    private VBox vBoxVideos;

    private Corpus corpus;

    @FXML
    private Label corpusName;

    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        // get the children of header component
        ObservableList<Node> childrenOfHeader = this.header.getChildren();
        // search for the btnHome and add a new onMouseClickListener
        for (var child : childrenOfHeader) {
            if (child.getId() != null && child.getId().equals("btnHome")) {
                child.setOnMouseClicked(event -> SceneManager.getSceneManager().changeScene("HomeScene.fxml"));
            }
        }


        // We get the corpus then update all the displayed information
        this.corpus = (Corpus) SceneManager.getSceneManager().getArgument("corpus");
        this.corpusName.setText(this.corpus.getName());
        UpdateFieldAudioList();
        UpdateImagesList();
        UpdateVideosList();
    }


    /**
     * Create FieldAudio List
     */
    private void UpdateFieldAudioList() {
        this.vBoxFieldAudios.getChildren().clear();

        Label labelTitle = new Label(resources.getString("FieldAudios"));
        labelTitle.setStyle("-fx-font-size: 20; -fx-text-fill: white; ");
        this.vBoxFieldAudios.getChildren().add(labelTitle);

        for(FieldAudio fa : this.corpus.getFieldAudios()) {

            // add the field audio
            Button btn = new Button(fa.getName());
            btn.getStyleClass().add("buttons");
            btn.getStyleClass().add("grey");
            btn.setPrefWidth(140);
            btn.setPrefHeight(50);

            // onClick on corpus
            btn.setOnMouseClicked(event -> {
                SceneManager.getSceneManager().addArgument("fieldAudio", fa);
                SceneManager.getSceneManager().addArgument("corpus", this.corpus);
                SceneManager.getSceneManager().changeScene("AudioEditScene.fxml");
            });

            this.vBoxFieldAudios.getChildren().add(btn);
        }

        Button btn = new Button(resources.getString("AddFieldAudio"));
        btn.getStyleClass().add("buttons");
        btn.getStyleClass().add("blue");
        btn.setPrefWidth(140);
        btn.setPrefHeight(50);

        // onClick on add field audio
        btn.setOnMouseClicked(event -> {
            this.corpus.createFieldAudio();
            UpdateFieldAudioList();
        });

        this.vBoxFieldAudios.getChildren().add(btn);
    }


    private void UpdateImagesList() {
        this.vBoxImages.getChildren().clear();

        Label labelTitle = new Label(resources.getString("Images"));
        labelTitle.setStyle("-fx-font-size: 20; -fx-text-fill: white; ");
        this.vBoxImages.getChildren().add(labelTitle);

        for(CorpusImage image : this.corpus.getCorpusImages()) {

            // add the Label
            Button btn = new Button(image.getName());
            btn.getStyleClass().add("buttons");
            btn.getStyleClass().add("grey");
            btn.setPrefWidth(140);
            btn.setPrefHeight(50);

            // onClick on corpus
            btn.setOnMouseClicked(event -> {
                SceneManager.getSceneManager().addArgument("image", image);
                SceneManager.getSceneManager().changeScene("ImageScene.fxml");
            });

            this.vBoxImages.getChildren().add(btn);
        }

        Button btn = new Button(resources.getString("AddFieldAudio"));
        btn.getStyleClass().add("buttons");
        btn.getStyleClass().add("blue");
        btn.setPrefWidth(140);
        btn.setPrefHeight(50);

        // onClick on add corpus image
        btn.setOnMouseClicked(event -> System.out.println("add image to corpus : TODO"));

        this.vBoxImages.getChildren().add(btn);
    }


    private void UpdateVideosList() {
        this.vBoxVideos.getChildren().clear();

        Label labelTitle = new Label(resources.getString("Videos"));
        labelTitle.setStyle("-fx-font-size: 20; -fx-text-fill: white; ");
        this.vBoxVideos.getChildren().add(labelTitle);

        for(CorpusVideo video : this.corpus.getCorpusVideos()) {

            // add the Label
            Button btn = new Button(video.getName());
            btn.getStyleClass().add("buttons");
            btn.getStyleClass().add("grey");
            btn.setPrefWidth(140);
            btn.setPrefHeight(50);

            // onClick on corpus
            btn.setOnMouseClicked(event -> {
                SceneManager.getSceneManager().addArgument("video", video);
                SceneManager.getSceneManager().changeScene("ImageScene.fxml");
            });

            this.vBoxVideos.getChildren().add(btn);
        }

        Button btn = new Button(resources.getString("AddFieldAudio"));
        btn.getStyleClass().add("buttons");
        btn.getStyleClass().add("blue");
        btn.setPrefWidth(140);
        btn.setPrefHeight(50);

        // onClick on add corpus video
        btn.setOnMouseClicked(event -> System.out.println("add video to corpus : TODO"));

        this.vBoxVideos.getChildren().add(btn);
    }

    /*
    @FXML
    protected void onAudioButtonClick() {
            FileChooser fileChooserAudio = new FileChooser();
            fileChooserAudio.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav")
            );
            File fileAudio = fileChooserAudio.showOpenDialog(null);
            if (fileAudio != null){
                SceneManager.getSceneManager().addArgument("loaded_file_audio", fileAudio);
                SceneManager.getSceneManager().changeScene("AudioEditScene.fxml");
            } else {
                Label audioErrorLabel = new Label("Ce fichier ne peut pas être chargé");
                Alert alert = new Alert(Alert.AlertType.ERROR, audioErrorLabel.getText());
                alert.showAndWait();
            }
    }

    @FXML
    protected void onImageButtonClick() {
        FileChooser fileChooserImage = new FileChooser();
        fileChooserImage.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("ImageFile", "*.jpg", "*.jpeg", "*.png")
        );
        File fileImage = fileChooserImage.showOpenDialog(null);
        if (fileImage != null){
            SceneManager.getSceneManager().addArgument("loaded_file_image", fileImage);
            SceneManager.getSceneManager().changeScene("ImageScene.fxml");
        } else {
            Label audioErrorLabel = new Label("Ce fichier ne peut pas être chargé");
            Alert alert = new Alert(Alert.AlertType.ERROR, audioErrorLabel.getText());
            alert.showAndWait();
        }
    }

    @FXML
    protected void onVideoButtonClick() {
        FileChooser fileChooserImage = new FileChooser();
        fileChooserImage.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("ImageFile", "*.mp4")
        );
        File fileImage = fileChooserImage.showOpenDialog(null);
        if (fileImage != null){
            SceneManager.getSceneManager().addArgument("loaded_file_video", fileImage);
            SceneManager.getSceneManager().changeScene("VideoScene.fxml");
        } else {
            Label audioErrorLabel = new Label("Ce fichier ne peut pas être chargé");
            Alert alert = new Alert(Alert.AlertType.ERROR, audioErrorLabel.getText());
            alert.showAndWait();
        }
    }

    */

    @FXML
    protected void onSettingsButtonClick() {
        SceneManager.getSceneManager().changeScene("SettingsScene.fxml");
    }
}