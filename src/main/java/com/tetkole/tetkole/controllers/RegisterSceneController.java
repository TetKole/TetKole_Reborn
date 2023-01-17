package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.HttpRequestManager;
import com.tetkole.tetkole.utils.SceneManager;
import com.tetkole.tetkole.utils.models.Corpus;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterSceneController implements Initializable {
    public HttpRequestManager apiManager = new HttpRequestManager();
    @FXML
    private TextField firstnameInput;

    @FXML
    private TextField lastnameInput;

    @FXML
    private TextField mailInput;

    @FXML
    private TextField passwordInput;

    @FXML
    private HBox header;

    @FXML
    public void onRegister() throws Exception {
        if (!firstnameInput.getText().isEmpty() && !lastnameInput.getText().isEmpty()
                && !mailInput.getText().isEmpty() && !passwordInput.getText().isEmpty())  {
            System.out.println(firstnameInput.getText());
            System.out.println(lastnameInput.getText());
            System.out.println(mailInput.getText());
            System.out.println(passwordInput.getText());
            apiManager.sendPostRegister(firstnameInput.getText(),lastnameInput.getText(),mailInput.getText(),passwordInput.getText());
        }
    }

    @FXML
    public void onGoToLogin() {
        SceneManager.getSceneManager().changeScene("LoginScene.fxml");
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
