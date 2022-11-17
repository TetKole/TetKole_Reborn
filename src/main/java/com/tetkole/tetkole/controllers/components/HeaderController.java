package com.tetkole.tetkole.controllers.components;


import javafx.fxml.FXML;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class HeaderController {

    /*
    @FXML
    public onHomeButtonClick() {
        //SceneManager.getSceneManager().changeScene("MainMenuScene.fxml");
    }*/

    @FXML
    public void onOpenFolderClick() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("windows")) {
            Desktop.getDesktop().open(new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\TetKole"));
        }
    }
}
