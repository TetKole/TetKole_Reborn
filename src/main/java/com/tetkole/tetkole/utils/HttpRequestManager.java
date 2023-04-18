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

import static java.lang.Integer.parseInt;


public class HttpRequestManager {

    private static HttpRequestManager httpRequestManagerInstance;
    private static String apiUrl;
    public static String servURL;
    private static final int STATUS_OK = 200;

    private HttpRequestManager(String servURL) {
        HttpRequestManager.servURL = servURL;
        HttpRequestManager.apiUrl = servURL + "/api";
    }

    public static HttpRequestManager getHttpRequestManagerInstance() {
        return httpRequestManagerInstance;
    }

    public static void setHttpRequestManagerInstance(String servURL) {
        HttpRequestManager.httpRequestManagerInstance = new HttpRequestManager(servURL);
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
                .header("Content-Type", "application/json")
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
                .header("Content-Type", "application/json")
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
                .header("Content-Type", "application/json")
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
    public JSONObject addDocument(int corpusId, File file, String docType, String token) {
        String uri = apiUrl + "/corpus/" + corpusId + "/addDocument";

        JSONObject answer = new JSONObject();
        answer.put("success", false);

        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpPost httppost = new HttpPost(uri);
            httppost.addHeader("Authorization", "Bearer " + token);

            final FileBody fileBody = new FileBody(file);
            final StringBody type = new StringBody(docType, ContentType.TEXT_PLAIN);

            final HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("file", fileBody)
                    .addPart("type", type)
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //System.out.println(answer);
        return answer;
    }


    // /api/document/{docID}/addAnnotation avec dans le body en form-data les fichiers et l'id de l'auteur.
    public JSONObject addAnnotation(File audioFile, File jsonFile, int docId, String token, int userId) {
        String uri = apiUrl + "/document/" + docId + "/addAnnotation";

        JSONObject answer = new JSONObject();
        answer.put("success", false);

        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpPost httppost = new HttpPost(uri);
            httppost.addHeader("Authorization", "Bearer " + token);

            final FileBody audioFileBody = new FileBody(audioFile);
            final FileBody jsonFileBody = new FileBody(jsonFile);
            final StringBody userIdBody = new StringBody(String.valueOf(userId), ContentType.TEXT_PLAIN);

            final HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("audioFile", audioFileBody)
                    .addPart("jsonFile", jsonFileBody)
                    .addPart("userId", userIdBody)
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
        } catch (IOException e) {
            throw new RuntimeException(e);
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
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .uri(URI.create(route))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject answer = new JSONObject();
        answer.put("success", response.statusCode() == STATUS_OK);
        answer.accumulate("body", response.body());

        return answer;
    }

    // /api/corpus/{id}/clone get the corpus_state of the corpus
    public JSONObject getCorpusState(String token, int corpusId) {
        String route = apiUrl + "/corpus/" + corpusId + "/clone";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .uri(URI.create(route))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        JSONObject answer = new JSONObject();
        answer.put("success", response.statusCode() == STATUS_OK);
        answer.accumulate("body", new JSONObject(response.body()));

        return answer;
    }


    public boolean deleteAnnotation(int documentId, int annotationId, String token) {
        String route = apiUrl + "/document/" + documentId + "/" + annotationId;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .uri(URI.create(route))
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JSONObject answer = new JSONObject(response.body());

        //System.out.println(answer);
        return answer.getBoolean("success");
    }

    public boolean renameAnnotation(int annotationId, String token, String newName) {
        String route = apiUrl + "/document/annotation/" + annotationId;
        JSONObject json = new JSONObject();
        json.put("newName", newName);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(String.valueOf(json)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .uri(URI.create(route))
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JSONObject answer = new JSONObject(response.body());

        //System.out.println(answer);
        System.out.println(answer);
        return answer.getBoolean("success");
    }

    public boolean deleteDocument(int documentId, String token) {
        String route = apiUrl + "/document/" + documentId;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .uri(URI.create(route))
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JSONObject answer = new JSONObject(response.body());

        //System.out.println(answer);
        return answer.getBoolean("success");
    }

    public boolean renameDocument(int documentId, String token, String newName) {
        String route = apiUrl + "/document/" + documentId;
        JSONObject json = new JSONObject();
        json.put("newName", newName);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(String.valueOf(json)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .uri(URI.create(route))
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JSONObject answer = new JSONObject(response.body());

        //System.out.println(answer);
        return answer.getBoolean("success");
    }

    public int getDocIdByName(String docName, String token) {
        String route = apiUrl + "/document/name/" + docName;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .uri(URI.create(route))
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return parseInt(response.body());
    }

    public boolean createNewVersionCorpus(String token, Integer corpusId) {
        String route = apiUrl + "/corpus/" + corpusId + "/createVersion";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .uri(URI.create(route))
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        if (response.statusCode() == 200) return true;

        return false;
    }

    public boolean updatePassword(String currentPassword, String newPassword) {
        String route = apiUrl + "/user/changePassword";
        JSONObject json = new JSONObject();
        json.put("mail", AuthenticationManager.getAuthenticationManager().getMail());
        json.put("password", currentPassword);
        json.put("newPassword", newPassword);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(json)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + AuthenticationManager.getAuthenticationManager().getToken())
                .uri(URI.create(route))
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JSONObject answer = new JSONObject(response.body());

        System.out.println(answer);
        return answer.getBoolean("success");
    }

    public boolean forceResetPassword(String userMail, String newPassword){
        String route = apiUrl + "/user/forceResetPassword";
        JSONObject json = new JSONObject();
        json.put("adminMail", AuthenticationManager.getAuthenticationManager().getMail());
        json.put("mail", userMail);
        json.put("newPassword", newPassword);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(json)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + AuthenticationManager.getAuthenticationManager().getToken())
                .uri(URI.create(route))
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JSONObject answer = new JSONObject(response.body());

        System.out.println(answer);
        return answer.getBoolean("success");
    }

    public JSONObject getAllUsersFromCorpus(Integer corpusId, String token) {
        String route = apiUrl + "/corpus/" + corpusId + "/users";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .uri(URI.create(route))
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JSONObject answer = new JSONObject();
        answer.put("success", response.statusCode() == STATUS_OK);
        answer.put("body", response.body());
        return answer;
    }
}