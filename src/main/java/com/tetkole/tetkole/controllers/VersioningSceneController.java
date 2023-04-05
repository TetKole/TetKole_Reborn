package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.*;
import com.tetkole.tetkole.utils.models.Corpus;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class VersioningSceneController implements Initializable {
    @FXML
    private HBox header;
    @FXML
    private VBox vBoxVersions;
    private Corpus corpus;
    @FXML
    private Label corpusName;
    @FXML
    private Label loadingLabelCreateVersion;
    @FXML
    private StackPane rootPane;
    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        // get the children of header component
        ObservableList<Node> childrenOfHeader = this.header.getChildren();
        // search for the btnHome and add a new onMouseClickListener
        for (var child : childrenOfHeader) {
            if (child.getId() != null && child.getId().equals("btnHome")) {
                child.setOnMouseClicked(event -> {
                    SceneManager.getSceneManager().addArgument("corpus", this.corpus);
                    SceneManager.getSceneManager().changeScene("CorpusMenuScene.fxml");
                });
            }
        }


        // We get the corpus then update all the displayed information
        this.corpus = (Corpus) SceneManager.getSceneManager().getArgument("corpus");
        this.corpusName.setText(this.corpus.getName());
        this.updateVersionsCorpus();
        // TODO cacher les btns si pas authentifié
        // Rémi: je pense qu'il vaut mieux carrément interdire l'accès a cette page si on est pas connecté
        // i.e. enelver le bouton Versioning du CorpusMenuScene si pas connecté
        // ou en fonction du role
    }

    public void onCreateNewVersion() {
        if (!AuthenticationManager.getAuthenticationManager().isAuthenticated()) return;

        if (this.corpus.getCorpusState() != null) {
            this.createNewVersion();
        }
    }

    private void updateVersionsCorpus() {
        this.vBoxVersions.getChildren().clear();

        Label labelTitle = new Label(resources.getString("CorpusVersions"));
        labelTitle.setStyle("-fx-font-size: 20; -fx-text-fill: white; ");
        this.vBoxVersions.getChildren().add(labelTitle);

        // TODO requeter pour avoir le numéro de dernière version
        List<String> versions = new ArrayList<>();

        for(String v : versions) {

            HBox line = new HBox();
            line.setAlignment(Pos.CENTER);
            line.setSpacing(20);

            // add the field audio
            Button btn = new Button(v);
            btn.getStyleClass().add("buttons");
            btn.getStyleClass().add("grey");
            btn.setPrefWidth(140);

            // onClick on corpus
            btn.setOnMouseClicked(event -> {
                // TODO download the corpus version
            });


            line.getChildren().add(btn);

            this.vBoxVersions.getChildren().add(line);
        }
    }

    private void createNewVersion() {
        this.loadingLabelCreateVersion.setVisible(true);
        System.out.println("Start Creating Version");
        LoadingManager.getLoadingManagerInstance().displayLoading(this.rootPane);


        new Thread(() -> {
            HttpRequestManager httpRequestManager = HttpRequestManager.getHttpRequestManagerInstance();
            String token = AuthenticationManager.getAuthenticationManager().getToken();
            final int corpusId = this.corpus.getCorpusId();

            if (!httpRequestManager.createNewVersionCorpus(token, corpusId)) {
                // TODO message d'erreur
            }

            LoadingManager.getLoadingManagerInstance().hideLoading(this.rootPane);
            this.loadingLabelCreateVersion.setVisible(false);
            System.out.println("Push Done");
            Platform.runLater(this::updateVersionsCorpus);
        }).start();
    }
}
