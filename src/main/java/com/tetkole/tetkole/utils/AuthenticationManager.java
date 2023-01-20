package com.tetkole.tetkole.utils;

import org.json.JSONObject;

import java.io.IOException;

public class AuthenticationManager {

    private static AuthenticationManager authenticationManagerInstance;

    private String JWT_TOKEN;
    private String firstname;
    private String lastname;
    private String mail;
    private int userId;
    private boolean isAuthenticated = false;

    private AuthenticationManager() {

    }

    public JSONObject login(String mail, String password) throws Exception {

        JSONObject response = HttpRequestManager.sendPostLogin(mail, password);

        JSONObject responseBody = new JSONObject(response.getString("body"));

        if (response.getBoolean("success")) {
            this.JWT_TOKEN = responseBody.getString("token");
            this.userId = responseBody.getInt("userId");
            this.firstname = responseBody.getString("firstname");
            this.lastname = responseBody.getString("lastname");
            this.mail = responseBody.getString("mail");
            this.isAuthenticated = true;
        }

        return response;
    }

    public JSONObject register(String firstname, String lastname, String mail, String password) throws IOException, InterruptedException {
        return HttpRequestManager.sendPostRegister(firstname, lastname, password, mail);
    }

    public static AuthenticationManager getAuthenticationManager() {
        return authenticationManagerInstance;
    }

    public static void setAuthenticationManager() {
        AuthenticationManager.authenticationManagerInstance = new AuthenticationManager();
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void disconnect() {
        this.JWT_TOKEN = null;
        this.userId = -1;
        this.mail = null;
        this.firstname = null;
        this.lastname = null;
        this.isAuthenticated = false;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }
}
