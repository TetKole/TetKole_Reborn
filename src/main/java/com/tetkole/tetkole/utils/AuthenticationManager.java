package com.tetkole.tetkole.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AuthenticationManager {

    private static AuthenticationManager authenticationManagerInstance;
    private String JWT_TOKEN;
    private String firstname;
    private String lastname;
    private String mail;
    private String role;
    private int userId;
    private boolean isAuthenticated = false;
    private Map<Integer, String> roles;

    private AuthenticationManager() { }

    public static AuthenticationManager getAuthenticationManager() {
        return authenticationManagerInstance;
    }

    public static void setAuthenticationManager() {
        AuthenticationManager.authenticationManagerInstance = new AuthenticationManager();
    }


    public JSONObject login(String mail, String password) {

        JSONObject response = null;
        try {
            response = HttpRequestManager.getHttpRequestManagerInstance().sendPostLogin(mail, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JSONObject body = response.getJSONObject("body");

        if (response.getBoolean("success")) {
            this.roles = toMap(body.getJSONObject("roles"));
            this.JWT_TOKEN = body.getString("token");
            this.userId = body.getInt("userId");
            this.firstname = body.getString("firstname");
            this.lastname = body.getString("lastname");
            this.mail = body.getString("mail");
            this.role = body.getString("role");
            this.isAuthenticated = true;
        }

        return response;
    }

    public JSONObject register(String firstname, String lastname, String mail, String password) {
        try {
            return HttpRequestManager.getHttpRequestManagerInstance().sendPostRegister(firstname, lastname, password, mail);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<Integer, String> toMap(JSONObject jsonobj)  throws JSONException {
        Map<Integer, String> map = new HashMap<>();
        Iterator<String> keys = jsonobj.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            String value = (String) jsonobj.get(key);
            map.put(Integer.valueOf(key), value);
        }   return map;
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
        this.role = null;
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

    public int getUserId() {
        return userId;
    }

    public String getMail() {
        return mail;
    }

    public String getRole() {
        return role;
    }
    public String getRole(Integer corpusId) {
        if (this.role.equals("ADMIN")){
            return "ADMIN";
        }
        return roles.getOrDefault(corpusId, "");
    }
}
