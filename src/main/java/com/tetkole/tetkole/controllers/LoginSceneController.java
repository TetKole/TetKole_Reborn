package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.HttpRequestManager;
import com.tetkole.tetkole.utils.SceneManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginSceneController implements Initializable {
    public HttpRequestManager apiManager = new HttpRequestManager();
    @FXML
    private TextField mailInput;

    @FXML
    private TextField passwordInput;

    @FXML
    public void onLogin() throws Exception {
        if (!mailInput.getText().isEmpty() && !passwordInput.getText().isEmpty())  {
            apiManager.sendPostLogin(mailInput.getText(),passwordInput.getText());
        }
    }

    @FXML
    private HBox header;

    @FXML
    public void onGoToRegister() {
        SceneManager.getSceneManager().changeScene("RegisterScene.fxml");
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        // get the children of header component
        ObservableList<Node> childrenOfHeader = this.header.getChildren();
        // search for the btnHome and add a new onMouseClickListener
        for (var child : childrenOfHeader) {
            if (child.getId() != null && child.getId().equals("btnHome")) {
                child.setOnMouseClicked(event -> SceneManager.getSceneManager().changeScene("HomeScene.fxml"));
            } else if (child.getId() != null && child.getId().equals("btnFolder")) {
                child.setVisible(false);
            }
        }
    }

}
