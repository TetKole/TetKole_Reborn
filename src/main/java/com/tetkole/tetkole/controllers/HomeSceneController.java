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

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HomeSceneController implements Initializable {

    @FXML
    private TextField corpusNameInput;
    @FXML
    private Label labelUserName;
    @FXML
    private VBox vBoxCorpus;
    @FXML
    private VBox vBoxButtons;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnRegister;

    @FXML
    private Button btnDisconnect;

    private List<Corpus> corpusList;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        UpdateCorpusList();
        isConnected();
    }

    @FXML
    public void onCreateCorpus() {
        if (!corpusNameInput.getText().isEmpty()) {
            Corpus.createCorpus(corpusNameInput.getText());
            UpdateCorpusList();
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
    }

    private void UpdateCorpusList() {
        this.vBoxCorpus.getChildren().clear();

        Label labelTitle = new Label("Corpus");
        labelTitle.setStyle("-fx-font-size: 20; -fx-text-fill: white; ");
        this.vBoxCorpus.getChildren().add(labelTitle);

        this.corpusList = Corpus.getAllCorpus();

        for(Corpus corpus : this.corpusList) {

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

    public void isConnected() {
        if (AuthenticationManager.getAuthenticationManager().isAuthenticated()) {

            vBoxButtons.getChildren().remove(btnLogin);
            vBoxButtons.getChildren().remove(btnRegister);
            labelUserName.setText(
                    AuthenticationManager.getAuthenticationManager().getFirstName() + " " +
                            AuthenticationManager.getAuthenticationManager().getLastName()
            );

        } else {
            vBoxButtons.getChildren().remove(btnDisconnect);
            vBoxButtons.getChildren().remove(labelUserName);
        }
    }
}
