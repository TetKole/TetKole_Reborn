package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.SceneManager;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.tetkole.tetkole.utils.RecordManager;
import javafx.scene.layout.HBox;


import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ImageSceneController implements Initializable {

    private String imageFileName;
    Image image;
    @FXML
    ImageView imageView;

    private RecordManager recordManager;

    private ResourceBundle resources;

    @FXML
    private Button btnRecord;

    @FXML
    private HBox header;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // get the children of header component
        ObservableList<Node> childrenOfHeader = this.header.getChildren();
        // search for the btnHome and add a new onMouseClickListener
        for (var child : childrenOfHeader) {
            if (child.getId() != null && child.getId().equals("btnHome")) {
                child.setOnMouseClicked(event -> {
                    this.imageView.setImage(null);
                    SceneManager.getSceneManager().changeScene("MainMenuScene.fxml");
                });
            }
        }

        this.resources = resources;



        this.recordManager = new RecordManager();

        // Get the file from the arguments and into image to show them in fxml
        File imageFile = (File) SceneManager.getSceneManager().getArgument("loaded_file_image");
        this.imageFileName = imageFile.getName();
        image = new Image(imageFile.toURI().toString());
        imageView.setImage(image);
    }

    @FXML
    protected void onRecordButtonClick() {
        if(recordManager.isRecording()) {
            this.recordManager.stopRecording();
            btnRecord.setText(resources.getString("StartRecord"));
            ((ImageView) btnRecord.getGraphic()).setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/record.png")).toExternalForm()));
        } else {
            this.recordManager.startRecording(imageFileName);
            btnRecord.setText(resources.getString("StopRecord"));
            ((ImageView) btnRecord.getGraphic()).setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/stopRecord.png")).toExternalForm()));
        }
    }
}
