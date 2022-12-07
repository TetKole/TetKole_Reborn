package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.SceneManager;

import com.tetkole.tetkole.utils.models.Corpus;
import com.tetkole.tetkole.utils.models.CorpusImage;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.tetkole.tetkole.utils.RecordManager;
import javafx.scene.layout.HBox;


import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;

public class ImageSceneController implements Initializable {
    private Corpus corpus;
    private CorpusImage image;
    private RecordManager recordManager;
    private ResourceBundle resources;

    // Graphics
    @FXML
    private Button btnRecord;
    @FXML
    private HBox header;
    @FXML
    ImageView imageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        // get the children of header component
        ObservableList<Node> childrenOfHeader = this.header.getChildren();
        // search for the btnHome and add a new onMouseClickListener
        for (var child : childrenOfHeader) {
            if (child.getId() != null && child.getId().equals("btnHome")) {
                child.setOnMouseClicked(event -> {
                    this.imageView.setImage(null);
                    SceneManager.getSceneManager().addArgument("corpus", this.corpus);
                    SceneManager.getSceneManager().changeScene("CorpusMenuScene.fxml");
                });
            }
        }


        this.recordManager = new RecordManager();

        // Get the file from the arguments and into image to show them in fxml
        this.corpus = (Corpus) SceneManager.getSceneManager().getArgument("corpus");
        this.image = (CorpusImage) SceneManager.getSceneManager().getArgument("image");

        imageView.setImage(new Image(image.getFile().toURI().toString()));
    }

    @FXML
    protected void onRecordButtonClick() {
        if (!recordManager.isRecording()) {
            // get the date for the record name
            String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("(dd-MM-yyyy_HH'h'mm'm'ss's')"));
            formattedDateTime = formattedDateTime.substring(1, formattedDateTime.length()-1);
            String recordName = "annotation_" + formattedDateTime + ".wav";

            String corpusPath = "/" + this.corpus.getName() + "/" + Corpus.folderNameAnnotation + "/" + this.image.getName();
            this.recordManager.startRecording(this.image.getName(), recordName, corpusPath, 0, 0);

            btnRecord.setText(resources.getString("StopRecord"));
            ((ImageView) btnRecord.getGraphic()).setImage(
                    new Image(Objects.requireNonNull(getClass().getResource("/images/stopRecord.png")).toExternalForm())
            );
        }
        else
        {
            this.recordManager.stopRecording(this.image);

            btnRecord.setText(resources.getString("StartRecord"));
            ((ImageView) btnRecord.getGraphic()).setImage(
                    new Image(Objects.requireNonNull(getClass().getResource("/images/record.png")).toExternalForm())
            );
        }
    }
}
