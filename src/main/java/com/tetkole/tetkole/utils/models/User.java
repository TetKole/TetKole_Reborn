package com.tetkole.tetkole.utils.models;

import org.json.JSONObject;

import java.util.Map;

public class User {
    private Integer userId;
    private String name;
    private String email;
    private String role;
    private Map<String, String> corpusRoles;

    public User(Integer userId, String name, String email, String role, Map<String, String> corpusRoles) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.corpusRoles = corpusRoles;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public Map<String, String> getCorpusRoles() {
        return corpusRoles;
    }

}
