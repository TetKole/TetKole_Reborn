package com.tetkole.tetkole.controllers.components;


import javafx.fxml.FXML;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class HeaderController {

    @FXML
    public void onOpenFolderClick() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        System.out.println(os);
        if (os.contains("windows")) {
            Desktop.getDesktop().open(new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\TetKole"));
        } else if(os.contains("linux")) {
            new Thread(() -> {
                try {
                    Desktop.getDesktop().open(new File("/home/" + System.getProperty("user.name") + "/TetKole/"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }
}
