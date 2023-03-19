package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.components.CustomButton;
import com.tetkole.tetkole.utils.AuthenticationManager;
import com.tetkole.tetkole.utils.FileManager;
import com.tetkole.tetkole.utils.HttpRequestManager;
import com.tetkole.tetkole.utils.SceneManager;
import com.tetkole.tetkole.utils.models.Corpus;
import com.tetkole.tetkole.utils.models.FieldAudio;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class VersionningSceneController implements Initializable {
    @FXML
    private HBox header;
    @FXML
    private VBox vBoxVersions;
    private Corpus corpus;
    @FXML
    private Label corpusName;
    @FXML
    private Label loadingLabelCreateVersion;
    private ResourceBundle resources;
    private volatile boolean newVersionThreadRunning;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
        // TODO remplir la liste des versions
        // TODO cacher les btns si pas authentifié
    }

    public void onCreateNewVersion() {
        if (!AuthenticationManager.getAuthenticationManager().isAuthenticated()) return;

        if (this.corpus.getCorpusState() != null) {
            this.createNewVersion();
            // TODO call and write this.updateVersionsCorpus()
            //this.updateVersionsCorpus();
        }
    }

    private void updateVersionsCorpus() {
        this.vBoxVersions.getChildren().clear();

        Label labelTitle = new Label(resources.getString("CorpusVersions"));
        labelTitle.setStyle("-fx-font-size: 20; -fx-text-fill: white; ");
        this.vBoxVersions.getChildren().add(labelTitle);

        // TODO requeter pour avoir le numéro de dernière version

        for(FieldAudio fa : this.corpus.getFieldAudios()) {

            HBox line = new HBox();
            line.setAlignment(Pos.CENTER);
            line.setSpacing(20);

            // add the field audio
            Button btn = new Button(fa.getName());
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
        // the push thread
        new Thread(() -> {

            HttpRequestManager httpRequestManager = HttpRequestManager.getHttpRequestManagerInstance();
            String token = AuthenticationManager.getAuthenticationManager().getToken();

            final int corpusId = this.corpus.getCorpusId();

            if(!httpRequestManager.createNewVersionCorpus(token, corpusId)){
                // TODO message d'erreur
            }

            this.newVersionThreadRunning = false;
        }).start();


        while (this.newVersionThreadRunning) {
            Thread.onSpinWait();
        }
        this.loadingLabelCreateVersion.setVisible(false);
    }
}
