package com.tetkole.tetkole.controllers.modals;

import com.tetkole.tetkole.utils.SceneManager;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

public class AddUserToCorpusModal implements Initializable {

    public TextField userMail;
    public ComboBox<ModerationType> roleChoice;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        roleChoice.getItems().addAll(
                new ModerationType("Lecteur", "reader"),
                new ModerationType("Contributeur", "contributor"),
                new ModerationType("Modérateur", "moderator")
        );
    }

    public void OnAddUserButtonClick() {
        String mail = userMail.getText();
        ModerationType moderationType = roleChoice.getValue();

        if(mail.isEmpty() || moderationType == null) {
            return;
        }

        JSONObject json = new JSONObject();
        json.put("userEmail", mail);
        json.put("userRole", moderationType.value());

        SceneManager.getSceneManager().closeModal(json.toString());
    }



    public record ModerationType(String label, String value) {

        @Override
            public String toString() {
                return label();
            }
        }

}
