package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.HttpRequestManager;
import com.tetkole.tetkole.utils.SceneManager;
import com.tetkole.tetkole.utils.enums.ToastTypes;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Random;

public class ModeratorSceneController implements PropertyChangeListener, Initializable {

    @FXML
    private TextField userMailInput;
    @FXML TextField userMailForAdminInput;
    @FXML
    private TextField newPassword;
    @FXML
    private TextField newUserMailInput;
    @FXML
    private HBox header;
    private ResourceBundle resources;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        // get the children of header component
        ObservableList<Node> childrenOfHeader = this.header.getChildren();
        // search for the btnHome and add a new onMouseClickListener
        for (var child : childrenOfHeader) {
            if (child.getId() != null && child.getId().equals("btnHome")) {
                child.setOnMouseClicked(event -> SceneManager.getSceneManager().changeScene("HomeScene.fxml"));
            }
        }
    }

    @FXML
    public void onChangePassword(){
        if (userMailInput.getText()!="") {
            try {
                String password = generatePassword(20);
                copyToClipboard(password);
                this.newPassword.setText(password);
                this.newPassword.setVisible(true);
                HttpRequestManager.getHttpRequestManagerInstance().forceResetPassword(userMailInput.getText(), password);
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    @FXML
    public void onAddMailInscription(){
        if (newUserMailInput.getText()!="") {
            try {
                if(HttpRequestManager.getHttpRequestManagerInstance().addMailInscription(newUserMailInput.getText())) {
                    SceneManager.getSceneManager().sendToast(resources.getString("MailAdded"), ToastTypes.SUCCESS);
                } else {
                    System.out.println("Impossible to add mail");
                    SceneManager.getSceneManager().sendToast(resources.getString("MailNotAdded"), ToastTypes.ERROR);
                }
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    @FXML
    public void onAddAdmin(){
        if (userMailForAdminInput.getText()!="") {
            try {
               System.out.println(userMailForAdminInput.getText());
                HttpRequestManager.getHttpRequestManagerInstance().addAdmin(userMailInput.getText());
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }
    }


    public static String generatePassword(int length) {
        String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        String NUMBERS = "0123456789";
        String SYMBOLS = "!@#$%&*()_+-=[]|,./?><";
        String allChars = CHAR_LOWER + CHAR_UPPER + NUMBERS + SYMBOLS;
        Random rand = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = rand.nextInt(allChars.length());
            password.append(allChars.charAt(randomIndex));
        }
        return password.toString();
    }

    public static void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }
}
