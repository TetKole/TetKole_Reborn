package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.AuthenticationManager;
import com.tetkole.tetkole.utils.LoadingManager;
import com.tetkole.tetkole.utils.SceneManager;
import com.tetkole.tetkole.utils.enums.ToastTypes;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterSceneController implements Initializable {
    @FXML
    private TextField firstnameInput;
    @FXML
    private TextField lastnameInput;
    @FXML
    private TextField mailInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private HBox header;
    @FXML
    private StackPane rootPane;
    private ResourceBundle resources;

    @FXML
    public void onRegister() {
        if (
                !firstnameInput.getText().isEmpty() &&
                !lastnameInput.getText().isEmpty() &&
                !mailInput.getText().isEmpty() &&
                !passwordInput.getText().isEmpty()
        )  {

            System.out.println("Start Register");
            LoadingManager.getLoadingManagerInstance().displayLoading(this.rootPane);

            new Thread(() -> {
                JSONObject response = AuthenticationManager.getAuthenticationManager().register(
                        firstnameInput.getText(),
                        lastnameInput.getText(),
                        mailInput.getText(),
                        passwordInput.getText()
                );

                System.out.println(response);

                if (response.getBoolean("success")) {
                    Platform.runLater(() -> {
                                SceneManager.getSceneManager().changeScene("LoginScene.fxml");
                                SceneManager.getSceneManager().sendToast(resources.getString("RegisterSuccessful"), ToastTypes.SUCCESS);
                            }
                    );
                } else {
                    System.out.println("Register Failed");
                    String wrongCredentialsText = SceneManager.getSceneManager().getResourceString("RegisterNotSuccessful");
                    SceneManager.getSceneManager().sendToast(wrongCredentialsText, ToastTypes.ERROR);
                }

                LoadingManager.getLoadingManagerInstance().hideLoading(this.rootPane);
                System.out.println("Register Done");
            }).start();
        }
    }

    @FXML
    public void onGoToLogin() {
        SceneManager.getSceneManager().changeScene("LoginScene.fxml");
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
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
