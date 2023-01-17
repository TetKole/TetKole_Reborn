package com.tetkole.tetkole.utils;


import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequestManager {

    private static final String baseUrl = "http://localhost:8000";


    //Post Request to register a user
    public void sendPostRegister(String firstname, String lastname, String mail, String password) throws Exception {

        String url = baseUrl+"/user";
        URL obj = new URL(url);
        //Open the connection
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Setting header and the Request Method
        con.setRequestMethod("POST");
        headerSetter(con);

        //Setting the json object for my user
        JSONObject json = new JSONObject();
        json.put("firstName", firstname);
        json.put("lastName", lastname);
        json.put("password", password);
        json.put("mail", mail);
        json.put("role", "default");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(String.valueOf(json));
        wr.flush();
        wr.close();

        //get the request response
        int responseCode = con.getResponseCode();
        responseDecoder(con,url);
    }

    //Post Request to login a user
    public void sendPostLogin(String mail,String password) throws Exception {

        // Setting header and the Request Method
        String url = baseUrl+"/user/login?mail="+mail+"&password="+password;
        URL obj = new URL(url);
        //Open the connection
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        headerSetter(con);

        con.setDoOutput(true);
        //get the request response
        responseDecoder(con,url);

    }

    public void responseDecoder(HttpURLConnection con, String url) throws IOException {
        int responseCode = con.getResponseCode();
        System.out.println("Sending 'POST' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();

        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
        //printing result from response
        System.out.println(response.toString());
    }

    //Function to set basic header for request
    public void headerSetter(HttpURLConnection con){
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type","application/json");
    }

}
