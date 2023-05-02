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

public class LoginSceneController implements Initializable {
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

    @FXML
    public void onLogin() {
        if (!mailInput.getText().isEmpty() && !passwordInput.getText().isEmpty())  {
            LoadingManager.getLoadingManagerInstance().displayLoading(this.rootPane);

            new Thread(() -> {
                JSONObject response = new JSONObject();
                try {
                    response = AuthenticationManager.getAuthenticationManager().login(mailInput.getText(), passwordInput.getText());
                } catch (RuntimeException e) {
                    LoadingManager.getLoadingManagerInstance().hideLoading(this.rootPane);
                }

                if (!response.isEmpty() && response.getBoolean("success")) {
                    Platform.runLater(() -> {
                                SceneManager.getSceneManager().changeScene("HomeScene.fxml");
                                SceneManager.getSceneManager().sendToast(resources.getString("LoginSuccessful"), ToastTypes.SUCCESS);
                            }
                    );
                } else {
                    String wrongCredentialsText = SceneManager.getSceneManager().getResourceString("ToastWrongUserCredentials");
                    SceneManager.getSceneManager().sendToast(wrongCredentialsText, ToastTypes.ERROR);
                }

                LoadingManager.getLoadingManagerInstance().hideLoading(this.rootPane);

            }).start();
        }
    }

    @FXML
    public void onGoToRegister() {
        SceneManager.getSceneManager().changeScene("RegisterScene.fxml");
    }

}
