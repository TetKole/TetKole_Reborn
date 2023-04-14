package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.utils.HttpRequestManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.TextField;

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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void onChangePassword(){
        if (userMailInput.getText()!="") {
            try {
                String password = generatePassword(20);
                copyToClipboard(password);
                HttpRequestManager.getHttpRequestManagerInstance().forceResetPassword(userMailInput.getText(), password);
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
