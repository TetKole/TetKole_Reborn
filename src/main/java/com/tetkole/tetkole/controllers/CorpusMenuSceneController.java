package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.AuthenticationManager;
import com.tetkole.tetkole.utils.HttpRequestManager;
import com.tetkole.tetkole.utils.SceneManager;
import com.tetkole.tetkole.utils.models.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

    @FXML
    private Label loadingLabel;

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
        updateFieldAudioList();
        updateImagesList();
        updateVideosList();
    }


    /**
     * Create FieldAudio List
     */
    private void updateFieldAudioList() {
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

        // onClick on add field audio
        btn.setOnMouseClicked(event -> {
            this.corpus.createFieldAudio();
            updateFieldAudioList();
        });

        this.vBoxFieldAudios.getChildren().add(btn);
    }


    private void updateImagesList() {
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

            // onClick on corpus
            btn.setOnMouseClicked(event -> {
                SceneManager.getSceneManager().addArgument("image", image);
                SceneManager.getSceneManager().addArgument("corpus", this.corpus);
                SceneManager.getSceneManager().changeScene("ImageScene.fxml");
            });

            this.vBoxImages.getChildren().add(btn);
        }

        Button btn = new Button(resources.getString("AddImage"));
        btn.getStyleClass().add("buttons");
        btn.getStyleClass().add("blue");
        btn.setPrefWidth(140);

        // onClick on add corpus image
        btn.setOnMouseClicked(event -> {
            this.corpus.createImage();
            updateImagesList();
        });

        this.vBoxImages.getChildren().add(btn);
    }


    private void updateVideosList() {
        this.vBoxVideos.getChildren().clear();

        Label labelTitle = new Label(resources.getString("Videos"));
        labelTitle.setStyle("-fx-font-size: 20; -fx-text-fill: white; ");
        this.vBoxVideos.getChildren().add(labelTitle);

        Label labelLimitVideo = new Label(resources.getString("LimitVideo"));
        labelLimitVideo.setStyle("-fx-font-size: 15; -fx-text-fill: white; -fx-text-alignment: CENTER");
        labelLimitVideo.setPrefWidth(240);
        labelLimitVideo.setWrapText(true);
        this.vBoxVideos.getChildren().add(labelLimitVideo);

        for(CorpusVideo video : this.corpus.getCorpusVideos()) {

            // add the Label
            Button btn = new Button(video.getName());
            btn.getStyleClass().add("buttons");
            btn.getStyleClass().add("grey");
            btn.setPrefWidth(140);

            // onClick on corpus
            btn.setOnMouseClicked(event -> {
                SceneManager.getSceneManager().addArgument("video", video);
                SceneManager.getSceneManager().addArgument("corpus", this.corpus);
                SceneManager.getSceneManager().changeScene("VideoScene.fxml");
            });

            this.vBoxVideos.getChildren().add(btn);
        }

        Button btn = new Button(resources.getString("AddVideo"));
        btn.getStyleClass().add("buttons");
        btn.getStyleClass().add("blue");
        btn.setPrefWidth(140);

        // onClick on add corpus video
        btn.setOnMouseClicked(event -> {
            this.corpus.createVideo();
            updateVideosList();
        });

        this.vBoxVideos.getChildren().add(btn);
    }


    public void pushInitCoprus() {
        if (!AuthenticationManager.getAuthenticationManager().isAuthenticated()) return;

        this.loadingLabel.setVisible(true);

        // the push init thread
        new Thread(() -> {

            // Get the infos we need
            HttpRequestManager httpRequestManager = HttpRequestManager.getHttpRequestManagerInstance();
            String token = AuthenticationManager.getAuthenticationManager().getToken();
            int userId = AuthenticationManager.getAuthenticationManager().getUserId();

            // Add Corpus

            JSONObject responseAddCorpus;
            try {
                responseAddCorpus = httpRequestManager.postAddCorpus(this.corpus.getName(), token);
            } catch (Exception e) { throw new RuntimeException(e); }

            // si success est false --> on va pas plus loin
            if (!responseAddCorpus.getBoolean("success")) {
                System.out.println("post add corpus failed, this corpus probably already exist on server");
                return;
            }

            final int corpusId = responseAddCorpus.getJSONObject("body").getInt("corpusId");
            System.out.println("POST addCorpus successfull. Corpus: " + this.corpus.getName() + " | Id: " + corpusId);



            // Add Documents

            List<Media> medias = new ArrayList<>(this.corpus.getFieldAudios());
            medias.addAll(this.corpus.getCorpusVideos());
            medias.addAll(this.corpus.getCorpusImages());

            for (Media m : medias) {
                String docType = "";
                if (m instanceof FieldAudio)  docType = "FieldAudio";
                if (m instanceof CorpusImage) docType = "Images";
                if (m instanceof CorpusVideo) docType = "Videos";

                JSONObject responseAddDocument;
                try {
                    responseAddDocument = httpRequestManager.addDocument(corpusId, m.getFile(), docType, token);
                } catch (Exception e) { throw new RuntimeException(e); }

                if (!responseAddDocument.getBoolean("success")) {
                    System.out.println("post add document failed");
                    return;
                }
                int docId = responseAddDocument.getJSONObject("body").getInt("docId");
                System.out.println("POST addDocument successfull. Document: " + m.getName() + " | Id: " + docId);

                // Add Annotations
                for (Annotation annotation : m.getAnnotations()) {

                    File jsonFile = annotation.getJsonFile();

                    JSONObject responseAddAnnotation;
                    try {
                        responseAddAnnotation = httpRequestManager.addAnnotation(annotation.getFile(), jsonFile, docId, token, userId);
                    } catch (Exception e) { throw new RuntimeException(e); }

                    if (!responseAddAnnotation.getBoolean("success")) {
                        System.out.println("post add document failed");
                        return;
                    }
                    System.out.println("POST addAnnotation successfull. Annotation: " + annotation.getFile().getName());
                }
            }

            loadingLabel.setVisible(false);

        }).start();
    }
}