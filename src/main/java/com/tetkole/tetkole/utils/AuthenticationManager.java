package com.tetkole.tetkole.utils;

import org.json.JSONObject;

import java.io.IOException;

public class AuthenticationManager {

    private static AuthenticationManager authenticationManagerInstance;

    private String JWT_TOKEN;
    private String firstName;
    private String lastName;
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
            this.firstName = responseBody.getString("firstName");
            this.lastName = responseBody.getString("lastName");
            this.mail = responseBody.getString("mail");
            this.isAuthenticated = true;
        }

        return response;
    }

    public JSONObject register(String firstName, String lastName, String mail, String password) throws IOException, InterruptedException {
        return HttpRequestManager.sendPostRegister(firstName, lastName, password, mail);
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
        this.firstName = null;
        this.lastName = null;
        this.isAuthenticated = false;
    }
}
