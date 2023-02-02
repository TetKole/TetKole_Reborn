package com.tetkole.tetkole.utils;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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

    public static HttpRequestManager getHttpRequestManagerInstance() {
        return httpRequestManagerInstance;
    }

    public static void setHttpRequestManagerInstance(String apiUrl) {
        HttpRequestManager.httpRequestManagerInstance = new HttpRequestManager(apiUrl);
    }


    //Post Request to register a user
    public JSONObject sendPostRegister(String firstname, String lastname, String password, String mail) throws Exception {
        JSONObject json = new JSONObject();
        json.put("firstname", firstname);
        json.put("lastname", lastname);
        json.put("password", password);
        json.put("mail", mail);
        json.put("role", "default");

        String routeUrl = apiUrl + "/user";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(json)))
                .header("Content-Type","application/json")
                .uri(URI.create(routeUrl))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject answer = new JSONObject();
        answer.accumulate("body", response.body());
        answer.put("success", response.statusCode() == STATUS_OK);

        return answer;
    }


    //Post Request to login a user
    public JSONObject sendPostLogin(String mail, String password) throws Exception {

        JSONObject json = new JSONObject();
        json.put("mail", mail);
        json.put("password", password);

        // Setting header and the Request Method
        String routeUrl = apiUrl + "/user/login";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(json)))
                .header("Content-Type","application/json")
                .uri(URI.create(routeUrl))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject answer = new JSONObject();
        answer.accumulate("body", new JSONObject(response.body()));
        answer.put("success", response.statusCode() == STATUS_OK);

        return answer;
    }


    public JSONObject postAddCorpus(String corpusName, String token) throws Exception {
        JSONObject body = new JSONObject();
        body.put("corpusName", corpusName);

        // Setting header and the Request Method
        String route = apiUrl + "/corpus";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(body)))
                .header("Content-Type","application/json")
                .header("Authorization", "Bearer " + token)
                .uri(URI.create(route))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject answer = new JSONObject();
        answer.accumulate("body", new JSONObject(response.body()));
        answer.put("success", response.statusCode() == STATUS_OK);

        System.out.println(answer.getJSONObject("body").get("name"));

        return answer;
    }


    // /api/corpus/{corpusId}/addDocument avec dans le body en form-data les fichiers.
    public JSONObject addDocument(int corpusId, File file, String token) throws Exception {

        /* VERSION 3 */

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(apiUrl + "/corpus/" + corpusId + "/addDocument");
        httpPost.addHeader("Authorization", "Bearer " + token);
        httpPost.addHeader("Accept", "*/*");
        httpPost.addHeader("Content-Type", "multipart/form-data; boundary=" + System.currentTimeMillis());

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addTextBody("type", "FieldAudio", ContentType.TEXT_PLAIN);
        builder.addTextBody("fileName", file.getName(), ContentType.TEXT_PLAIN);

        // This attaches the file to the POST:
        builder.addBinaryBody(
                "file",
                new FileInputStream(file),
                ContentType.DEFAULT_BINARY,
                file.getName()
        );

        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();

        System.out.println(responseEntity);

        /* VERSION 2
        String boundary = "===" + System.currentTimeMillis() + "===";

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        //builder.addPart("file", new FileBody(file));
        //builder.addBinaryBody("file", file);
        builder.addTextBody("type", "FieldAudio");
        builder.addTextBody("fileName", file.getName());
        HttpEntity entity = builder.build();

        HttpPost httppost = new HttpPost(apiUrl + "/corpus/" + corpusId + "/addDocument");
        httppost.addHeader("Authorization", "Bearer " + token);
        httppost.addHeader("Accept", "application/json");
        httppost.addHeader("Content-Type", "multipart/form-data; boundary=" + boundary);



        httppost.setEntity(entity);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        org.apache.http.HttpResponse response = httpClient.execute(httppost);

        System.out.println(response);
        */



        /* VERSION 1
        JSONObject body = new JSONObject();
        body.put("type", "FieldAudio");
        body.put("fileName", file.getName());
        body.put("file", file);

        // creates a unique boundary based on time stamp
        String boundary = "===" + System.currentTimeMillis() + "===";

        // Setting header and the Request Method
        String route = apiUrl + "/corpus/" + corpusId + "/addDocument";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(body)))
                .header("Content-Type","multipart/form-data; boundary" + boundary)
                .header("Authorization", "Bearer " + token)
                .uri(URI.create(route))
                .build();



        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        System.out.println(route);
        System.out.println(response.body());


        */


        return null;
    }
}
