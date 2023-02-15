package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.AuthenticationManager;
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
            System.out.println("not auth");
            System.out.println(this.vBoxCorpusServer.getChildren().size());
        } else {
            String token = AuthenticationManager.getAuthenticationManager().getToken();

            JSONObject jsonCorpus;
            try {
                jsonCorpus = HttpRequestManager.getHttpRequestManagerInstance().getCorpusList(token);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println(jsonCorpus.toString());

            if (jsonCorpus.get("body").toString().length() > 0) {
                JSONArray array = new JSONArray(jsonCorpus.get("body").toString());
                for (int i = 0; i < array.length(); i++) {

                    String corpusName = array.get(i).toString();
                    // add the Label
                    Button btn = new Button(corpusName);
                    btn.getStyleClass().add("buttons");
                    btn.getStyleClass().add("grey");
                    btn.setPrefWidth(140);
                    btn.setPrefHeight(50);

                    btn.setOnMouseClicked(event -> {
                        // TODO CLONE HERE
                        // genre un appel de fonction a la méthode clone
                        // private void clone(String corpusName);
                        System.out.println("Corpus name : " + corpusName);
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
