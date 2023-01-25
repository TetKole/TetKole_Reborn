package com.tetkole.tetkole.components;

import javafx.beans.NamedArg;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class CustomButton extends Button {

    @FXML
    public ImageView imageView;
    private final String imageUrl;

    public CustomButton(String graphicUrl) {
        URL url = getClass().getResource("custom_button.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        this.imageUrl = graphicUrl;
        setImage(this.imageUrl);
    }

    public void setImage(String imageUrl) {
        Image img = new Image(imageUrl);
        this.imageView.setImage(img);
    }

    public void resizeImage(int size) {
        this.setPrefHeight(size);
        this.setPrefWidth(size);
    }
}
