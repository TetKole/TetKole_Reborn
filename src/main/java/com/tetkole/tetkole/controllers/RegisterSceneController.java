package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.SceneManager;
import com.tetkole.tetkole.utils.models.Corpus;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class RegisterSceneController {

    @FXML
    private TextField firstnameInput;

    @FXML
    private TextField lastnameInput;

    @FXML
    private TextField mailInput;

    @FXML
    private TextField passwordInput;


    @FXML
    public void onRegister() {
        if (!firstnameInput.getText().isEmpty() && !lastnameInput.getText().isEmpty()
                && !mailInput.getText().isEmpty() && !passwordInput.getText().isEmpty())  {
            System.out.println(firstnameInput.getText());
            System.out.println(lastnameInput.getText());
            System.out.println(mailInput.getText());
            System.out.println(passwordInput.getText());
        }
    }

    @FXML
    public void onGoToLogin() {
        SceneManager.getSceneManager().changeScene("LoginScene.fxml");
    }
}