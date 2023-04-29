package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.components.CustomButton;
import com.tetkole.tetkole.utils.AuthenticationManager;
import com.tetkole.tetkole.utils.HttpRequestManager;
import com.tetkole.tetkole.utils.SceneManager;
import com.tetkole.tetkole.utils.models.Corpus;
import com.tetkole.tetkole.utils.models.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

public class CorpusModerationSceneController implements Initializable {
    private ResourceBundle resources;
    private Corpus corpus;
    private ArrayList<User> users = new ArrayList<>();
    @FXML
    private GridPane usersList;
    @FXML
    private Label corpusTitle;
    @FXML
    private StackPane rootPane;
    @FXML GridPane labels;
    @FXML
    private HBox header;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        this.corpus = (Corpus) SceneManager.getSceneManager().getArgument("corpus");
        this.corpusTitle.setText(this.corpus.getName());

        // get the children of header component
        ObservableList<Node> childrenOfHeader = this.header.getChildren();
        // search for the btnHome and add a new onMouseClickListener
        for (var child : childrenOfHeader) {
            if (child.getId() != null && child.getId().equals("btnHome")) {
                child.setOnMouseClicked(event -> {
                    SceneManager.getSceneManager().addArgument("corpus", this.corpus);
                    SceneManager.getSceneManager().changeScene("CorpusMenuScene.fxml");
                });
            }
        }

        requestUserList();

        refreshList();

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(25);
        for (int i = 0; i < 4; i++) {
            usersList.getColumnConstraints().add(col1);
            labels.getColumnConstraints().add(col1);
        }
        usersList.setVgap(10);

        this.rootPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            usersList.setPrefWidth((double)newValue * 0.8);
            labels.setPrefWidth((double)newValue * 0.8);
            //usersList.setHgap(usersList.getPrefWidth() / 4);
            //labels.setHgap(usersList.getPrefWidth() / 4);
            refreshList();
        });

        this.rootPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            usersList.setPrefHeight((double)newValue * 0.6);
            refreshList();
        });
    }

    private void requestUserList() {
        users.clear();

        String token = AuthenticationManager.getAuthenticationManager().getToken();
        JSONObject response = HttpRequestManager.getHttpRequestManagerInstance().getAllUsersFromCorpus(corpus.getCorpusId(), token);
        System.out.println(response.getString("body"));
        JSONArray usersJSONArray = new JSONArray(response.getString("body"));
        for (int i = 0; i < usersJSONArray.length(); i++) {
            JSONObject userObject = usersJSONArray.getJSONObject(i);
            System.out.println(userObject.toString());

            JSONObject corpusRoles = userObject.getJSONObject("corpusRoles");

            HashMap<String, String> mapCorpusRoles = new HashMap<>();
            for(String key: corpusRoles.keySet()) {
                mapCorpusRoles.put(key, corpusRoles.getString(key));
            }

            User user = new User(
                    userObject.getInt("userId"),
                    userObject.getString("name"),
                    userObject.getString("email"),
                    userObject.getString("role"),
                    mapCorpusRoles
            );
            users.add(user);
        }
    }

    private void refreshList() {
        usersList.getChildren().clear();
        for (int i = 0; i < users.size(); i++) {
            addUserLine(users.get(i));
        }
    }

    private void addUserLine(User user) {

        if(!user.getUserId().equals(AuthenticationManager.getAuthenticationManager().getUserId())){
            Label userName = new Label(user.getName());
            userName.setStyle("-fx-text-fill: white;");
            VBox userNameVbox = new VBox(userName);
            userNameVbox.setAlignment(Pos.CENTER);

            Label email = new Label(user.getEmail());
            email.setStyle("-fx-text-fill: white;");
            VBox emailVbox = new VBox(email);
            emailVbox.setAlignment(Pos.CENTER);

            Label role = new Label(user.getCorpusRoles().get(corpus.getName()));
            role.setStyle("-fx-text-fill: white;");
            VBox roleVbox = new VBox(role);
            roleVbox.setAlignment(Pos.CENTER);

            CustomButton btnDelete = new CustomButton(Objects.requireNonNull(getClass().getResource("/images/error.png")).toExternalForm());
            btnDelete.setOnAction(e -> {
                HttpRequestManager.getHttpRequestManagerInstance().deleteUserFromCorpus(
                        this.corpus.getCorpusId(),
                        user.getUserId(),
                        AuthenticationManager.getAuthenticationManager().getToken()
                );
                this.requestUserList();
                this.refreshList();
            });

            VBox actions = new VBox(btnDelete);
            actions.setAlignment(Pos.CENTER);

            usersList.addColumn(0, userNameVbox);
            usersList.addColumn(1, emailVbox);
            usersList.addColumn(2, roleVbox);
            usersList.addColumn(3, actions);
        }
    }

    public void addNewUser(ActionEvent actionEvent) {
        String returnParameter = SceneManager.getSceneManager().showNewModal("modals/AddUserToCorpusModal.fxml", "userNotAdded", "test");

        if(returnParameter.equals("userNotAdded")) {
            return;
        }

        JSONObject json = new JSONObject(returnParameter);

        json = HttpRequestManager.getHttpRequestManagerInstance()
                .addUserToCorpus(corpus.getCorpusId(), json.getString("userEmail"), json.getString("userRole"));

        System.out.println(json);

        requestUserList();

        refreshList();
    }
}
