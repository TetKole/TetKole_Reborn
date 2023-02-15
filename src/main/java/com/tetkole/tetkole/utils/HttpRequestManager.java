package com.tetkole.tetkole.utils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;


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
        answer.put("success", response.statusCode() == STATUS_OK);
        answer.accumulate("body", new JSONObject(response.body()));

        //System.out.println(answer);
        return answer;
    }


    // /api/corpus/{corpusId}/addDocument avec dans le body en form-data les fichiers.
    public JSONObject addDocument(int corpusId, File file, String docType, String token) throws Exception {
        String uri = apiUrl + "/corpus/" + corpusId + "/addDocument";

        JSONObject answer = new JSONObject();
        answer.put("success", false);

        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpPost httppost = new HttpPost(uri);
            httppost.addHeader("Authorization", "Bearer " + token);

            final FileBody fileBody = new FileBody(file);
            final StringBody type = new StringBody(docType, ContentType.TEXT_PLAIN);
            final StringBody fileName = new StringBody(file.getName(), ContentType.TEXT_PLAIN);

            final HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("file", fileBody)
                    .addPart("type", type)
                    .addPart("fileName", fileName)
                    .build();

            httppost.setEntity(reqEntity);

            httpclient.execute(httppost, response -> {
                // get the info from the response
                final int responseCode = response.getStatusLine().getStatusCode();
                final String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                // close the request
                EntityUtils.consume(response.getEntity());

                // construct return value
                answer.put("success", responseCode == STATUS_OK);
                answer.accumulate("body", new JSONObject(responseBody));

                return true;
            });
        }

        //System.out.println(answer);
        return answer;
    }


    // /api/document/addAnnotation avec dans le body en form-data les fichiers et le doc name.
    public JSONObject addAnnotation(File audioFile, File jsonFile, String documentName, String token) throws Exception {
        String uri = apiUrl + "/document/addAnnotation";

        JSONObject answer = new JSONObject();
        answer.put("success", false);

        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpPost httppost = new HttpPost(uri);
            httppost.addHeader("Authorization", "Bearer " + token);

            final FileBody audioFileBody = new FileBody(audioFile);
            final FileBody jsonFileBody = new FileBody(jsonFile);
            final StringBody documentNameBody = new StringBody(documentName, ContentType.TEXT_PLAIN);

            final HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("audioFile", audioFileBody)
                    .addPart("jsonFile", jsonFileBody)
                    .addPart("documentName", documentNameBody)
                    .build();

            httppost.setEntity(reqEntity);

            httpclient.execute(httppost, response -> {
                // get the info from the response
                final int responseCode = response.getStatusLine().getStatusCode();
                final String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                // close the request
                EntityUtils.consume(response.getEntity());

                // construct return value
                answer.put("success", responseCode == STATUS_OK);
                answer.accumulate("body", new JSONObject(responseBody));

                return true;
            });
        }

        //System.out.println(answer);
        return answer;
    }

    // /api/corpus/list get all corpus name in a list
    public JSONObject getCorpusList(String token) throws Exception {
        String route = apiUrl + "/corpus/list";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Content-Type","application/json")
                .header("Authorization", "Bearer " + token)
                .uri(URI.create(route))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject answer = new JSONObject();
        answer.put("success", response.statusCode() == STATUS_OK);
        answer.accumulate("body", response.body());

        //System.out.println(answer);
        return answer;
    }
}