package com.tetkole.tetkole.controllers.components;

import com.tetkole.tetkole.Main;
import com.tetkole.tetkole.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Header extends HBox {

    @FXML
    public Button btnHome;

    @FXML
    public Button btnReturn;


    public Header() {
        URL url = Main.class.getResource("components/header.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        loader.setResources(ResourceBundle.getBundle("languages", SceneManager.getSceneManager().getCurrentLocale()));
        loader.setRoot(this);
        loader.setController(this);


        try {
            loader.load();
            setup();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }


    private void setup() {
        btnHome.setOnAction(e -> {
            onHome();
        });

        btnReturn.setOnAction(e -> {
            onReturn();
        });

        // A enlever une fois le onReturn() fait
        btnReturn.setDisable(true);
    }

    @FXML
    public void onHome() {
        SceneManager.getSceneManager().changeScene("main_menu.fxml");
    }

    @FXML
    public void onReturn() {
        //TODO finish onReturn in Header
        System.out.println("TODO");
    }
}
