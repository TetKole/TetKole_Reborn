package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.AuthenticationManager;
import com.tetkole.tetkole.utils.FileManager;
import com.tetkole.tetkole.utils.HttpRequestManager;
import com.tetkole.tetkole.utils.SceneManager;
import com.tetkole.tetkole.utils.models.Corpus;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.*;

public class HomeSceneController implements Initializable {

    @FXML
    private TextField corpusNameInput;
    @FXML
    private Label labelUserName;
    @FXML
    private VBox vBoxCorpus;

    @FXML
    private VBox vBoxCorpusServer;
    @FXML
    private VBox vBoxButtons;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnRegister;

    @FXML
    private Button btnDisconnect;

    private List<Corpus> corpusList;

    private ResourceBundle resources;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isConnected();
        this.resources = resources;
        updateCorpusList();
        updateCorpusListServer();
    }

    @FXML
    public void onCreateCorpus() {
        if (!corpusNameInput.getText().isEmpty()) {
            Corpus.createCorpus(corpusNameInput.getText());
            updateCorpusList();
        }
    }

    @FXML
    public void onGoToSettings() {
        SceneManager.getSceneManager().changeScene("SettingsScene.fxml");
    }

    @FXML
    public void onGoToLogin() {
        SceneManager.getSceneManager().changeScene("LoginScene.fxml");
    }

    @FXML
    public void onGoToRegister() {
        SceneManager.getSceneManager().changeScene("RegisterScene.fxml");
    }

    @FXML
    public void onDisconnect() {
        AuthenticationManager.getAuthenticationManager().disconnect();
        vBoxButtons.getChildren().remove(btnDisconnect);
        vBoxButtons.getChildren().remove(labelUserName);
        vBoxButtons.getChildren().add(btnLogin);
        vBoxButtons.getChildren().add(btnRegister);
        updateCorpusListServer();
    }

    private void updateCorpusList() {
        this.vBoxCorpus.getChildren().clear();

        Label labelTitle = new Label("Corpus");
        labelTitle.setStyle("-fx-font-size: 20; -fx-text-fill: white; ");
        this.vBoxCorpus.getChildren().add(labelTitle);

        this.corpusList = Corpus.getAllCorpus();

        for (Corpus corpus : this.corpusList) {

            // add the Label
            Button btn = new Button(corpus.getName());
            btn.getStyleClass().add("buttons");
            btn.getStyleClass().add("grey");
            btn.setPrefWidth(140);
            btn.setPrefHeight(50);

            // onClick on corpus
            btn.setOnMouseClicked(event -> {
                SceneManager.getSceneManager().addArgument("corpus", corpus);
                SceneManager.getSceneManager().changeScene("CorpusMenuScene.fxml");
            });

            vBoxCorpus.getChildren().add(btn);
        }
    }

    private void updateCorpusListServer() {
        this.vBoxCorpusServer.getChildren().clear();

        Label labelTitleServer = new Label(resources.getString("CorpusServerTitle"));
        labelTitleServer.setStyle("-fx-font-size: 20; -fx-text-fill: white; ");
        this.vBoxCorpusServer.getChildren().add(labelTitleServer);

        if (!AuthenticationManager.getAuthenticationManager().isAuthenticated()) {
            Label labelNeedAuth = new Label(resources.getString("CorpusListNeedAuth"));
            labelNeedAuth.setStyle("-fx-font-size: 20; -fx-text-fill: white; ");
            this.vBoxCorpusServer.getChildren().add(labelNeedAuth);
        } else {
            String token = AuthenticationManager.getAuthenticationManager().getToken();

            JSONObject jsonCorpus;
            try {
                jsonCorpus = HttpRequestManager.getHttpRequestManagerInstance().getCorpusList(token);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (jsonCorpus.get("body").toString().length() > 0) {
                JSONArray corpusList = new JSONArray(jsonCorpus.get("body").toString());

                for (Object corpusString : corpusList) {
                    JSONObject corpusJSON = new JSONObject(corpusString.toString());
                    String corpusName = corpusJSON.getString("name");

                    // add the Label
                    Button btn = new Button(corpusName);
                    btn.getStyleClass().add("buttons");
                    btn.getStyleClass().add("grey");
                    btn.setPrefWidth(140);
                    btn.setPrefHeight(50);

                    btn.setOnMouseClicked(event -> {
                        try {
                            // Get corpus_state.json from server
                            JSONObject responseClone = HttpRequestManager.getHttpRequestManagerInstance().getCorpusState(token, corpusJSON.getInt("corpusId"));

                            // TODO faire en sorte si les fichiers ne se télécharge pas en entier quand la co crash
                            JSONObject corpus_content = new JSONObject(responseClone.get("body").toString());
                            // Create folders for new corpus
                            Corpus.createCorpus(corpusName);

                            // Create corpus_state.json
                            File corpus_state = FileManager.getFileManager().createFile(corpusName, "corpus_state.json");
                            FileManager.getFileManager().writeJSONFile(corpus_state, corpus_content);

                            // Pour chaque document, télécharger le doc et le move dans le dossier de son type et créer un dossier dans Annotation
                            JSONArray documents = corpus_content.getJSONArray("documents");

                            for (int i = 0; i < documents.length(); i++) {
                                JSONObject document_json = documents.getJSONObject(i);
                                FileManager.getFileManager().downloadDocument(
                                            document_json.getString("type"),
                                            corpusName,
                                            document_json.getString("name")
                                        );
                                JSONArray annotations = document_json.getJSONArray("annotations");
                                for (int j = 0; j < annotations.length(); j++) {
                                    JSONObject annotation_json = annotations.getJSONObject(j);
                                    FileManager.getFileManager().downloadAnnotation(
                                            corpusName,
                                            document_json.getString("name"),
                                            annotation_json.getString("name")
                                    );
                                }
                                // TODO revoir le système des annotation écrites
                                if(document_json.getString("type").equals(Corpus.folderNameFieldAudio)) {
                                    String fieldAudioJsonName = document_json.getString("name").split("\\.")[0] + ".json";
                                    File fieldAudioJson = FileManager.getFileManager().createFile(corpusName + "/" + Corpus.folderNameFieldAudio, fieldAudioJsonName);
                                    JSONObject fieldAudioJsonContent = new JSONObject();
                                    fieldAudioJsonContent.put("fileName", document_json.getString("name"));
                                    fieldAudioJsonContent.put("description", "");
                                    FileManager.getFileManager().writeJSONFile(fieldAudioJson, fieldAudioJsonContent);
                                }
                            }

                            // Update corpus list
                            updateCorpusList();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                    vBoxCorpusServer.getChildren().add(btn);

                }
            }
        }
    }

    public void isConnected() {
        if (AuthenticationManager.getAuthenticationManager().isAuthenticated()) {
            vBoxButtons.getChildren().remove(btnLogin);
            vBoxButtons.getChildren().remove(btnRegister);
            labelUserName.setText(
                    AuthenticationManager.getAuthenticationManager().getFirstname() + " " +
                            AuthenticationManager.getAuthenticationManager().getLastname()
            );
        } else {
            vBoxButtons.getChildren().remove(btnDisconnect);
            vBoxButtons.getChildren().remove(labelUserName);
            vBoxCorpusServer.getChildren().clear();
        }
    }
}
