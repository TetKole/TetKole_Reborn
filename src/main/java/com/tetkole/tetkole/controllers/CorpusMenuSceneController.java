package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.components.CustomButton;
import com.tetkole.tetkole.utils.AuthenticationManager;
import com.tetkole.tetkole.utils.FileManager;
import com.tetkole.tetkole.utils.HttpRequestManager;
import com.tetkole.tetkole.utils.SceneManager;
import com.tetkole.tetkole.utils.models.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class CorpusMenuSceneController implements Initializable {

    @FXML
    private HBox header;

    @FXML
    private VBox vBoxFieldAudios;
    @FXML
    private VBox vBoxImages;
    @FXML
    private VBox vBoxVideos;

    private Corpus corpus;

    @FXML
    private Label corpusName;

    @FXML
    private Label loadingLabelPush;

    @FXML
    private Label loadingLabelPull;

    private ResourceBundle resources;

    private volatile boolean pullThreadRunning;
    private JSONObject tempCorpusStateForPull;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        // get the children of header component
        ObservableList<Node> childrenOfHeader = this.header.getChildren();
        // search for the btnHome and add a new onMouseClickListener
        for (var child : childrenOfHeader) {
            if (child.getId() != null && child.getId().equals("btnHome")) {
                child.setOnMouseClicked(event -> SceneManager.getSceneManager().changeScene("HomeScene.fxml"));
            }
        }


        // We get the corpus then update all the displayed information
        this.corpus = (Corpus) SceneManager.getSceneManager().getArgument("corpus");
        this.corpusName.setText(this.corpus.getName());
        updateFieldAudioList();
        updateImagesList();
        updateVideosList();
    }


    /**
     * Create FieldAudio List
     */
    private void updateFieldAudioList() {
        this.vBoxFieldAudios.getChildren().clear();

        Label labelTitle = new Label(resources.getString("FieldAudios"));
        labelTitle.setStyle("-fx-font-size: 20; -fx-text-fill: white; ");
        this.vBoxFieldAudios.getChildren().add(labelTitle);

        for(FieldAudio fa : this.corpus.getFieldAudios()) {

            HBox line = new HBox();
            line.setAlignment(Pos.CENTER);
            line.setSpacing(20);

            // add the field audio
            Button btn = new Button(fa.getName());
            btn.getStyleClass().add("buttons");
            btn.getStyleClass().add("grey");
            btn.setPrefWidth(140);

            // onClick on corpus
            btn.setOnMouseClicked(event -> {
                SceneManager.getSceneManager().addArgument("fieldAudio", fa);
                SceneManager.getSceneManager().addArgument("corpus", this.corpus);
                SceneManager.getSceneManager().changeScene("AudioEditScene.fxml");
            });

            CustomButton btnEdit = new CustomButton(Objects.requireNonNull(getClass().getResource("/images/edit.png")).toExternalForm());

            btnEdit.setOnMouseClicked(event -> {
                //TODO
            });

            line.getChildren().add(btn);
            line.getChildren().add(btnEdit);

            this.vBoxFieldAudios.getChildren().add(line);
        }

        Button btn = new Button(resources.getString("AddFieldAudio"));
        btn.getStyleClass().add("buttons");
        btn.getStyleClass().add("blue");
        btn.setPrefWidth(140);

        // onClick on add field audio
        btn.setOnMouseClicked(event -> {
            this.corpus.createFieldAudio();
            updateFieldAudioList();
        });

        this.vBoxFieldAudios.getChildren().add(btn);
    }


    private void updateImagesList() {
        this.vBoxImages.getChildren().clear();

        Label labelTitle = new Label(resources.getString("Images"));
        labelTitle.setStyle("-fx-font-size: 20; -fx-text-fill: white; ");
        this.vBoxImages.getChildren().add(labelTitle);

        for(CorpusImage image : this.corpus.getCorpusImages()) {

            HBox line = new HBox();
            line.setAlignment(Pos.CENTER);
            line.setSpacing(20);

            // add the Label
            Button btn = new Button(image.getName());
            btn.getStyleClass().add("buttons");
            btn.getStyleClass().add("grey");
            btn.setPrefWidth(140);

            // onClick on corpus
            btn.setOnMouseClicked(event -> {
                SceneManager.getSceneManager().addArgument("image", image);
                SceneManager.getSceneManager().addArgument("corpus", this.corpus);
                SceneManager.getSceneManager().changeScene("ImageScene.fxml");
            });

            CustomButton btnEdit = new CustomButton(Objects.requireNonNull(getClass().getResource("/images/edit.png")).toExternalForm());

            btnEdit.setOnMouseClicked(event -> {
                //TODO
            });

            line.getChildren().add(btn);
            line.getChildren().add(btnEdit);

            this.vBoxFieldAudios.getChildren().add(line);
        }

        Button btn = new Button(resources.getString("AddImage"));
        btn.getStyleClass().add("buttons");
        btn.getStyleClass().add("blue");
        btn.setPrefWidth(140);

        // onClick on add corpus image
        btn.setOnMouseClicked(event -> {
            this.corpus.createImage();
            updateImagesList();
        });

        this.vBoxImages.getChildren().add(btn);
    }


    private void updateVideosList() {
        this.vBoxVideos.getChildren().clear();

        Label labelTitle = new Label(resources.getString("Videos"));
        labelTitle.setStyle("-fx-font-size: 20; -fx-text-fill: white; ");
        this.vBoxVideos.getChildren().add(labelTitle);

        Label labelLimitVideo = new Label(resources.getString("LimitVideo"));
        labelLimitVideo.setStyle("-fx-font-size: 15; -fx-text-fill: white; -fx-text-alignment: CENTER");
        labelLimitVideo.setPrefWidth(240);
        labelLimitVideo.setWrapText(true);
        this.vBoxVideos.getChildren().add(labelLimitVideo);

        for(CorpusVideo video : this.corpus.getCorpusVideos()) {

            HBox line = new HBox();
            line.setAlignment(Pos.CENTER);
            line.setSpacing(20);

            // add the Label
            Button btn = new Button(video.getName());
            btn.getStyleClass().add("buttons");
            btn.getStyleClass().add("grey");
            btn.setPrefWidth(140);

            // onClick on corpus
            btn.setOnMouseClicked(event -> {
                SceneManager.getSceneManager().addArgument("video", video);
                SceneManager.getSceneManager().addArgument("corpus", this.corpus);
                SceneManager.getSceneManager().changeScene("VideoScene.fxml");
            });

            CustomButton btnEdit = new CustomButton(Objects.requireNonNull(getClass().getResource("/images/edit.png")).toExternalForm());

            btnEdit.setOnMouseClicked(event -> {
                //TODO
            });

            line.getChildren().add(btn);
            line.getChildren().add(btnEdit);

            this.vBoxFieldAudios.getChildren().add(line);
        }

        Button btn = new Button(resources.getString("AddVideo"));
        btn.getStyleClass().add("buttons");
        btn.getStyleClass().add("blue");
        btn.setPrefWidth(140);

        // onClick on add corpus video
        btn.setOnMouseClicked(event -> {
            this.corpus.createVideo();
            updateVideosList();
        });

        this.vBoxVideos.getChildren().add(btn);
    }


    public void pushInitCorpus() {
        if (!AuthenticationManager.getAuthenticationManager().isAuthenticated()) return;

        this.loadingLabelPush.setVisible(true);

        // the push init thread
        new Thread(() -> {

            // Get the infos we need
            HttpRequestManager httpRequestManager = HttpRequestManager.getHttpRequestManagerInstance();
            String token = AuthenticationManager.getAuthenticationManager().getToken();
            int userId = AuthenticationManager.getAuthenticationManager().getUserId();

            // Add Corpus

            JSONObject responseAddCorpus;
            try {
                responseAddCorpus = httpRequestManager.postAddCorpus(this.corpus.getName(), token);
            } catch (Exception e) { throw new RuntimeException(e); }

            // si success est false --> on va pas plus loin
            if (!responseAddCorpus.getBoolean("success")) {
                System.out.println("post add corpus failed, this corpus probably already exist on server");
                return;
            }

            final int corpusId = responseAddCorpus.getJSONObject("body").getInt("corpusId");
            System.out.println("POST addCorpus successfull. Corpus: " + this.corpus.getName() + " | Id: " + corpusId);



            // Add Documents

            List<Media> medias = new ArrayList<>(this.corpus.getFieldAudios());
            medias.addAll(this.corpus.getCorpusVideos());
            medias.addAll(this.corpus.getCorpusImages());

            // TODO prendre en compte si la co crash
            for (Media m : medias) {
                String docType = "";
                if (m instanceof FieldAudio)  docType = "FieldAudio";
                if (m instanceof CorpusImage) docType = "Images";
                if (m instanceof CorpusVideo) docType = "Videos";

                JSONObject responseAddDocument;
                try {
                    responseAddDocument = httpRequestManager.addDocument(corpusId, m.getFile(), docType, token);
                } catch (Exception e) { throw new RuntimeException(e); }

                if (!responseAddDocument.getBoolean("success")) {
                    System.out.println("post add document failed");
                    return;
                }
                int docId = responseAddDocument.getJSONObject("body").getInt("docId");
                System.out.println("POST addDocument successfull. Document: " + m.getName() + " | Id: " + docId);

                // Add Annotations
                for (Annotation annotation : m.getAnnotations()) {

                    File jsonFile = annotation.getJsonFile();

                    JSONObject responseAddAnnotation;
                    try {
                        responseAddAnnotation = httpRequestManager.addAnnotation(annotation.getFile(), jsonFile, docId, token, userId);
                    } catch (Exception e) { throw new RuntimeException(e); }

                    if (!responseAddAnnotation.getBoolean("success")) {
                        System.out.println("post add document failed");
                        return;
                    }
                    System.out.println("POST addAnnotation successfull. Annotation: " + annotation.getFile().getName());
                }
            }

            JSONObject responseClone;
            try {
                // Get corpus_state.json from server
                responseClone = httpRequestManager.getCorpusState(token, corpusId);
                JSONObject corpus_content = new JSONObject(responseClone.get("body").toString());

                // Create corpus_state.json
                File corpus_state = FileManager.getFileManager().createFile(this.corpus.getName(), "corpus_state.json");
                FileManager.getFileManager().writeJSONFile(corpus_state, corpus_content);
            } catch (Exception e) { throw new RuntimeException(e); }

            loadingLabelPush.setVisible(false);

        }).start();
    }


    public void pullCorpus() {
        if (!AuthenticationManager.getAuthenticationManager().isAuthenticated()) return;
        System.out.println("Start Pulling");
        this.loadingLabelPull.setVisible(true);

        pullThreadRunning = true;

        new Thread(() -> {
            try {
                // Get the infos we need
                HttpRequestManager httpRequestManager = HttpRequestManager.getHttpRequestManagerInstance();
                String token = AuthenticationManager.getAuthenticationManager().getToken();


                JSONObject localCorpusState = this.corpus.getCorpusState();
                int corpusId = localCorpusState.getInt("corpusId");

                // Get corpus_state.json from server
                JSONObject responseGetCorpusState = httpRequestManager.getCorpusState(token, corpusId);

                if (!responseGetCorpusState.getBoolean("success")) {
                    System.out.println("error when fetching Corpus State from server");
                    return;
                }

                JSONObject serverCorpusState = responseGetCorpusState.getJSONObject("body");

                this.tempCorpusStateForPull = serverCorpusState;

                /*
                System.out.println("serverCorpusState");
                System.out.println(serverCorpusState);
                System.out.println("localCorpusState");
                System.out.println(localCorpusState);
                */

                // on compare les deux corpus state
                JSONArray serveurDocs = serverCorpusState.getJSONArray("documents");
                JSONArray localDocs = localCorpusState.getJSONArray("documents");
                JSONObject diffDocs = new JSONObject();
                diffDocs.put("documents", new JSONArray());
                diffDocs.put("annotations", new JSONArray());

                /*
                diffDocs
                {
                    "documents": [
                        {
                            "id": 2,
                            "type": "Images",
                            "name": "image.jpg"
                        },
                        {
                            "id": 4,
                            "type": "FieldAudio",
                            "name": "audio.mp3"
                        }
                    ],
                    "annotations": [
                        {
                            "id": 2,
                            "name": "annotation_15-02-2023_11h40m34s_939.wav",
                            "document": "audio.mp3"
                        }
                    ]
                }*/

                // on regarde si les docs sur serveur existe en local
                for (int i=0; i < serveurDocs.length(); i++) {
                    JSONObject doc = serveurDocs.getJSONObject(i);

                    // si le doc existe pas, on l'ajoute a diffDocs
                    boolean docExistOnLocal = existOnDocArray(doc, localDocs);
                    if (!docExistOnLocal) {
                        JSONObject newDoc = new JSONObject();
                        newDoc.put("id", doc.getInt("docId"));
                        newDoc.put("name", doc.getString("name"));
                        newDoc.put("type", doc.get("type"));
                        diffDocs.getJSONArray("documents").put(newDoc);
                    }

                    // idem pour les annotations
                    JSONArray serverAnnotations = doc.getJSONArray("annotations");
                    // si le doc exist en local, on compare les tableaux d'annotation
                    // sinon, on ajoute toutes les annotations du serveur sur le diffDocs
                    if (docExistOnLocal) {
                        JSONArray localAnnotations = findLocalAnnotations(doc, localDocs);
                        for (int j=0; j<serverAnnotations.length(); j++) {
                            JSONObject a = serverAnnotations.getJSONObject(j);
                            if (!existOnAnnotationArray(a, localAnnotations)) {
                                JSONObject newAnnotation = new JSONObject();
                                newAnnotation.put("id", a.get("annotationId"));
                                newAnnotation.put("name", a.get("name"));
                                newAnnotation.put("document", doc.get("name"));
                                diffDocs.getJSONArray("annotations").put(newAnnotation);
                            }
                        }
                    } else {
                        for(int j=0; j<serverAnnotations.length(); j++) {
                            JSONObject annotation = serverAnnotations.getJSONObject(j);
                            JSONObject newAnnotation = new JSONObject();
                            newAnnotation.put("id", annotation.get("annotationId"));
                            newAnnotation.put("name", annotation.get("name"));
                            newAnnotation.put("document", doc.get("name"));
                            diffDocs.getJSONArray("annotations").put(newAnnotation);
                        }
                    }

                }

                fetchCorpusDiff(diffDocs);

                pullThreadRunning = false;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }).start();

        while (pullThreadRunning) {
            Thread.onSpinWait();
        }

        this.loadingLabelPull.setVisible(false);

        FileManager fileManager = FileManager.getFileManager();
        File newCorpusState = new File(fileManager.getFolderPath() + "/" + this.corpus.getName() + "/corpus_state.json");
        fileManager.writeJSONFile(newCorpusState, this.tempCorpusStateForPull);
        this.corpus.reload();

        updateVideosList();
        updateImagesList();
        updateFieldAudioList();

        System.out.println("Pull Done");
    }

    private boolean existOnDocArray(JSONObject docOnServer, JSONArray docs) {
        int id = docOnServer.getInt("docId");
        String name = docOnServer.getString("name");

        for (int i=0; i < docs.length(); i++) {
            JSONObject doc = docs.getJSONObject(i);
            if (doc.getInt("docId") == id && doc.getString("name").equals(name)) {
                return true;
            }
        }
        return false;
    }

    private JSONArray findLocalAnnotations(JSONObject doc, JSONArray localDocs) {
        int id = doc.getInt("docId");
        String name = doc.getString("name");

        for (int i=0; i<localDocs.length(); i++) {
            if (localDocs.getJSONObject(i).getString("name").equals(name) && localDocs.getJSONObject(i).getInt("docId") == id) {
                return localDocs.getJSONObject(i).getJSONArray("annotations");
            }
        }
        return new JSONArray();
    }

    private boolean existOnAnnotationArray(JSONObject a, JSONArray annotations) {
        int id = a.getInt("annotationId");
        String name = a.getString("name");

        for (int i=0; i<annotations.length(); i++) {
            if (annotations.getJSONObject(i).getInt("annotationId") == id && annotations.getJSONObject(i).getString("name").equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get diffDocs from server and place them correctly in corpus file system, then update corpus_state.
     * @param diffDocs docs to fetch from server
     */
    private void fetchCorpusDiff(JSONObject diffDocs) {
         /*
            diffDocs
            {
                "documents": [
                    {
                        "id": 2,
                        "type": "Images",
                        "name": "image.jpg"
                    },
                    {
                        "id": 4,
                        "type": "FieldAudio",
                        "name": "audio.mp3"
                    }
                ],
                "annotations": [
                    {
                        "id": 2,
                        "name": "annotation_15-02-2023_11h40m34s_939.wav",
                        "document": "audio.mp3"
                    }
                ]
            }*/

        //System.out.println("diffDocs");
        //System.out.println(diffDocs);

        FileManager fileManager = FileManager.getFileManager();

        JSONArray documents = diffDocs.getJSONArray("documents");
        JSONArray annotations = diffDocs.getJSONArray("annotations");

        try {
            for (int i=0; i<documents.length(); i++) {
                String fileName = documents.getJSONObject(i).getString("name");
                String type = documents.getJSONObject(i).getString("type");

                fileManager.downloadDocument(type, this.corpus.getName(), fileName);
            }

            for (int i=0; i<annotations.length(); i++) {
                String fileName = annotations.getJSONObject(i).getString("name");
                String docName = annotations.getJSONObject(i).getString("document");

                fileManager.downloadAnnotation(this.corpus.getName(), docName, fileName);
            }

            this.loadingLabelPull.setVisible(false);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}