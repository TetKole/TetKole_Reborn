package com.tetkole.tetkole.utils;
import org.json.JSONObject;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class HttpRequestManager{

    private static HttpRequestManager httpRequestManagerInstance;
    private static String apiUrl;

    private static final int STATUS_OK = 200;

    private HttpRequestManager(String apiUrl) {
        HttpRequestManager.apiUrl = apiUrl;
    }

    //Post Request to register a user
    public static JSONObject sendPostRegister(String firstname, String lastname, String password, String mail) throws IOException, InterruptedException {
        JSONObject json = new JSONObject();
        json.put("firstName", firstname);
        json.put("lastName", lastname);
        json.put("password", password);
        json.put("mail", mail);
        json.put("role", "default");

        String routeUrl = apiUrl+"/user";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(json)))
                .header("Content-Type","application/json")
                .uri(URI.create(routeUrl))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject answer = new JSONObject();
        answer.put("body", response.body());
        answer.put("success", response.statusCode() == STATUS_OK);

        return answer;
    }


    //Post Request to login a user
    public static JSONObject sendPostLogin(String mail, String password) throws Exception {

        JSONObject json = new JSONObject();
        json.put("mail", mail);
        json.put("password", password);

        // Setting header and the Request Method
        String routeUrl = apiUrl+"/user/login";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(json)))
                .header("Content-Type","application/json")
                .uri(URI.create(routeUrl))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject answer = new JSONObject();
        answer.put("body", response.body());
        answer.put("success", response.statusCode() == STATUS_OK);

        return answer;

    }

    public static HttpRequestManager getHttpRequestManagerInstance() {
        return httpRequestManagerInstance;
    }

    public static void setHttpRequestManagerInstance(String apiUrl) {
        HttpRequestManager.httpRequestManagerInstance = new HttpRequestManager(apiUrl);
    }
}
