package com.tetkole.tetkole.components;


import com.tetkole.tetkole.utils.AuthenticationManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HeaderController implements Initializable {

    @FXML
    public Label labelUserName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        AuthenticationManager instance = AuthenticationManager.getAuthenticationManager();

        if (instance.isAuthenticated()) {
            labelUserName.setText(instance.getFirstName() + " " + instance.getLastName());
        }

    }

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
