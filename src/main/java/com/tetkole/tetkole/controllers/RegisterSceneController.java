package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.HttpRequestManager;
import com.tetkole.tetkole.utils.SceneManager;
import com.tetkole.tetkole.utils.models.Corpus;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class RegisterSceneController {
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
    public void onRegister() throws Exception {
        if (!firstnameInput.getText().isEmpty() && !lastnameInput.getText().isEmpty()
                && !mailInput.getText().isEmpty() && !passwordInput.getText().isEmpty())  {
            apiManager.sendPostRegister(firstnameInput.getText(),lastnameInput.getText(),mailInput.getText(),passwordInput.getText());
        }
    }

    @FXML
    public void onGoToLogin() {
        SceneManager.getSceneManager().changeScene("LoginScene.fxml");
    }
}
