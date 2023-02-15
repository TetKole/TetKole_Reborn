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

    private AuthenticationManager() { }

    public static AuthenticationManager getAuthenticationManager() {
        return authenticationManagerInstance;
    }

    public static void setAuthenticationManager() {
        AuthenticationManager.authenticationManagerInstance = new AuthenticationManager();
    }


    public JSONObject login(String mail, String password) throws Exception {

        JSONObject response = HttpRequestManager.getHttpRequestManagerInstance().sendPostLogin(mail, password);

        JSONObject body = response.getJSONObject("body");

        if (response.getBoolean("success")) {
            this.JWT_TOKEN = body.getString("token");
            this.userId = body.getInt("userId");
            this.firstname = body.getString("firstname");
            this.lastname = body.getString("lastname");
            this.mail = body.getString("mail");
            this.isAuthenticated = true;
        }

        return response;
    }

    public JSONObject register(String firstname, String lastname, String mail, String password) throws Exception {
        return HttpRequestManager.getHttpRequestManagerInstance().sendPostRegister(firstname, lastname, password, mail);
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

    public String getToken() {
        return this.JWT_TOKEN;
    }

    public int getUserId() { return userId; }
}
