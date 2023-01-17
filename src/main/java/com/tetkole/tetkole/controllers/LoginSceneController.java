package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.HttpRequestManager;
import com.tetkole.tetkole.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginSceneController {
    public HttpRequestManager apiManager = new HttpRequestManager();
    @FXML
    private TextField mailInput;

    @FXML
    private TextField passwordInput;

    @FXML
    public void onLogin() throws Exception {
        if (!mailInput.getText().isEmpty() && !passwordInput.getText().isEmpty())  {
            System.out.println(mailInput.getText());
            System.out.println(passwordInput.getText());
            apiManager.sendPostLogin(mailInput.getText(),passwordInput.getText());
        }
    }

    @FXML
    public void onGoToRegister() {
        SceneManager.getSceneManager().changeScene("RegisterScene.fxml");
    }


}
