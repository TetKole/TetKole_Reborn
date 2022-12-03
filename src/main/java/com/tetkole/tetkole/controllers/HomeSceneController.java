package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.SceneManager;
import com.tetkole.tetkole.utils.models.Corpus;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HomeSceneController implements Initializable {

    @FXML
    private TextField corpusNameInput;
    @FXML
    private VBox vBoxCorpus;

    private List<Corpus> corpusList;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UpdateCorpusList();
    }

    @FXML
    public void onCreateCorpus() {
        if (!corpusNameInput.getText().isEmpty()) {
            Corpus.createCorpus(corpusNameInput.getText());
            UpdateCorpusList();
        }
    }

    private void UpdateCorpusList() {
        vBoxCorpus.getChildren().clear();

        Label labelTitle = new Label("Corpus");
        labelTitle.setStyle("-fx-font-size: 16; -fx-text-fill: white;");

        vBoxCorpus.getChildren().add(labelTitle);

        this.corpusList = Corpus.getAllCorpus();

        for(Corpus corpus : corpusList) {

            // add the Label
            Button btn = new Button(corpus.getName());
            btn.getStyleClass().add("buttons");
            btn.getStyleClass().add("grey");

            btn.setOnMouseClicked(event -> {
                SceneManager.getSceneManager().addArgument("corpus", corpus);
                SceneManager.getSceneManager().changeScene("MainMenuScene.fxml");
            });

            vBoxCorpus.getChildren().add(btn);
        }
    }
}
