package com.tetkole.tetkole.utils.models;

public class User {
    private Integer userId;
    private String name;
    private String email;
    private String role;

    public User(Integer userId, String name, String email, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
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
}
