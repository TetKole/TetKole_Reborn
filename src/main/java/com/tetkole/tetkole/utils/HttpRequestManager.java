package com.tetkole.tetkole.utils;
import org.json.JSONObject;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class HttpRequestManager{

    private static final String baseUrl = "http://localhost:8000";


    private static final String testUrl ="https://jsonplaceholder.typicode.com/posts";
    public static void exemplegetRequest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Content-Type","application/json")
                .uri(URI.create(testUrl))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
    }


    //Post Request to register a user
    public static void sendPostRegister(String firstname, String lastname, String password, String mail) throws IOException, InterruptedException {
        JSONObject json = new JSONObject();
        json.put("firstName", firstname);
        json.put("lastName", lastname);
        json.put("password", password);
        json.put("mail", mail);
        json.put("role", "default");

        String routeUrl = baseUrl+"/user";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(json)))
                .header("Content-Type","application/json")
                .uri(URI.create(routeUrl))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
    }


    //Post Request to login a user
    public void sendPostLogin(String mail,String password) throws Exception {

        // Setting header and the Request Method
        String routeUrl = baseUrl+"/user/login?mail="+mail+"&password="+password;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf("")))
                .header("Content-Type","application/json")
                .uri(URI.create(routeUrl))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());

    }

}
