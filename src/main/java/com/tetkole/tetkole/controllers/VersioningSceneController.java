package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.*;
import com.tetkole.tetkole.utils.enums.ToastTypes;
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
import org.json.JSONObject;

import java.io.IOException;
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

        int currentVersion = HttpRequestManager.getHttpRequestManagerInstance().getCurrentVersionCorpus(AuthenticationManager.getAuthenticationManager().getToken(), corpus.getCorpusId());

        for (int i = 1; i <= currentVersion; i++) {

            HBox line = new HBox();
            line.setAlignment(Pos.CENTER);
            line.setSpacing(20);

            // add the field audio
            Button btn = new Button(String.valueOf(i));
            btn.getStyleClass().add("buttons");
            btn.getStyleClass().add("grey");
            btn.setPrefWidth(140);

            // onClick on corpus
            int finalI = i;
            btn.setOnMouseClicked(event -> {
                LoadingManager.getLoadingManagerInstance().displayLoading(this.rootPane);
                new Thread(() -> {
                    if(FileManager.getFileManager().downloadVersion(corpus.getName(), finalI)) {
                        Platform.runLater(() -> {
                                    SceneManager.getSceneManager().sendToast(resources.getString("DownloadVersion"), ToastTypes.SUCCESS);
                                }
                        );
                    } else {
                        Platform.runLater(() -> {
                                    SceneManager.getSceneManager().sendToast(resources.getString("DownloadVersionError"), ToastTypes.ERROR);
                                }
                        );
                    }
                    LoadingManager.getLoadingManagerInstance().hideLoading(this.rootPane);
                    this.loadingLabelCreateVersion.setVisible(false);
                }).start();
            });

            line.getChildren().add(btn);

            this.vBoxVersions.getChildren().add(line);
        }
    }

    private void createNewVersion() {
        this.loadingLabelCreateVersion.setVisible(true);
        LoadingManager.getLoadingManagerInstance().displayLoading(this.rootPane);


        new Thread(() -> {
            HttpRequestManager httpRequestManager = HttpRequestManager.getHttpRequestManagerInstance();
            String token = AuthenticationManager.getAuthenticationManager().getToken();
            final int corpusId = this.corpus.getCorpusId();
            JSONObject response = null;
            try {
                response = httpRequestManager.createNewVersionCorpus(token, corpusId);
            } catch (Exception e) {

            }

            if(response != null) {
                if (response.getBoolean("success")) {
                    Platform.runLater(this::updateVersionsCorpus);
                    Platform.runLater(() -> {
                                SceneManager.getSceneManager().sendToast(resources.getString("VersionCreated"), ToastTypes.SUCCESS);
                            }
                    );
                } else {
                    Platform.runLater(() -> {
                                SceneManager.getSceneManager().sendToast(resources.getString("VersionErrorClient"), ToastTypes.ERROR);
                            }
                    );
                }
            } else {
                Platform.runLater(() -> {
                            SceneManager.getSceneManager().sendToast(resources.getString("VersionErrorServer"), ToastTypes.ERROR);
                        }
                );
            }
            LoadingManager.getLoadingManagerInstance().hideLoading(this.rootPane);
            this.loadingLabelCreateVersion.setVisible(false);
        }).start();
    }
}
