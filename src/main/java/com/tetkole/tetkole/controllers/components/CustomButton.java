package com.tetkole.tetkole.controllers.components;

import javafx.beans.NamedArg;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;

public class CustomButton extends Button {
    @FXML private ImageView imageView;
    private final String imageUrl;
    @FXML
    protected void initialize() {
        /*imageView.setImage(new Image(imageUrl));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(495. / 6);
        this.setContentDisplay(ContentDisplay.TOP);*/
    }

    public CustomButton(@NamedArg("graphicUrl") String graphicUrl) {
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
        System.out.println(graphicUrl);
    }
}
