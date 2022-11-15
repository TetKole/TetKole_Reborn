package com.tetkole.tetkole.controllers.components;

import com.tetkole.tetkole.utils.SceneManager;
import javafx.fxml.FXML;


public class HeaderController {

    @FXML public void onHomeButtonClick() {
        SceneManager.getSceneManager().changeScene("MainMenuScene.fxml");
    }
}
