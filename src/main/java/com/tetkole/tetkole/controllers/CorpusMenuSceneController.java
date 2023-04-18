package com.tetkole.tetkole.controllers;

import com.tetkole.tetkole.components.CustomButton;
import com.tetkole.tetkole.utils.*;
import com.tetkole.tetkole.utils.models.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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

    @FXML
    private StackPane rootPane;
    @FXML
    private HBox hboxTopButtons;
    @FXML
    private Button goToModerationBtn;
    @FXML
    private VBox vBoxTopButtonsContainer;

    private ResourceBundle resources;


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

        isConnected();
    }


    private void isConnected() {
        if (AuthenticationManager.getAuthenticationManager().isAuthenticated()) {
            String role = AuthenticationManager.getAuthenticationManager().getRole(corpus.getCorpusId());
            if (!role.equals("ADMIN") && !role.equals("MODERATOR")) {
                hboxTopButtons.getChildren().remove(goToModerationBtn);
            }
        } else {
            vBoxTopButtonsContainer.getChildren().remove(hboxTopButtons);
        }
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
                this.renameDoc(fa, corpus);
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
                this.renameDoc(image, corpus);
            });

            line.getChildren().add(btn);
            line.getChildren().add(btnEdit);

            this.vBoxImages.getChildren().add(line);
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
                this.renameDoc(video, corpus);
            });

            line.getChildren().add(btn);
            line.getChildren().add(btnEdit);

            this.vBoxVideos.getChildren().add(line);
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


    public void onClickPush() {
        if (!AuthenticationManager.getAuthenticationManager().isAuthenticated()) return;

        if (this.corpus.getCorpusState() != null) {
            this.push();
        } else {
            this.pushInit();
        }
    }

    /**
     * Push your change on the server if you have the same corpus_state than the server.
     */
    private void push() {
        System.out.println("Start Push");
        LoadingManager.getLoadingManagerInstance().displayLoading(this.rootPane);
        this.loadingLabelPush.setVisible(true);

        // the push thread
        new Thread(() -> {

            HttpRequestManager httpRequestManager = HttpRequestManager.getHttpRequestManagerInstance();
            String token = AuthenticationManager.getAuthenticationManager().getToken();

            final int corpusId = this.corpus.getCorpusId();
            final JSONObject localState = this.corpus.getCorpusState();

            // Get corpus_state.json from server
            JSONObject responseClone = httpRequestManager.getCorpusState(token, corpusId);
            JSONObject serverState = new JSONObject(responseClone.get("body").toString());

            if ((serverState.toString()).equals(localState.toString())) {
                // you can push

                JSONObject modifs = this.corpus.getCorpusModif();

                JSONArray deletedAnnotations = modifs.getJSONObject("deleted").getJSONArray("annotations");
                JSONArray deletedDocuments = modifs.getJSONObject("deleted").getJSONArray("documents");
                JSONArray addedDocuments = modifs.getJSONObject("added").getJSONArray("documents");
                JSONArray addedAnnotations = modifs.getJSONObject("added").getJSONArray("annotations");
                JSONArray updatedDocuments = modifs.getJSONObject("updated").getJSONArray("documents");
                JSONArray updatedAnnotations = modifs.getJSONObject("updated").getJSONArray("annotations");

                // step 1 : update annotations
                for (int i=0; i<updatedAnnotations.length(); i++) {
                    JSONObject annotation = updatedAnnotations.getJSONObject(i);
                    httpRequestManager.renameAnnotation(annotation.getInt("id"), token, annotation.getString("newName"));
                }

                // step 2 : update document
                for (int i=0; i<updatedDocuments.length(); i++) {
                    JSONObject document = updatedDocuments.getJSONObject(i);
                    httpRequestManager.renameDocument(document.getInt("id"), token, document.getString("newName"));
                }

                // step 3 : delete annotations
                for (int i=0; i<deletedAnnotations.length(); i++) {
                    JSONObject annotation = deletedAnnotations.getJSONObject(i);
                    httpRequestManager.deleteAnnotation(annotation.getInt("docId"), annotation.getInt("id"), token);
                }

                // step 4 : delete document
                for (int i=0; i<deletedDocuments.length(); i++) {
                    JSONObject document = deletedDocuments.getJSONObject(i);
                    httpRequestManager.deleteDocument(document.getInt("id"), token);
                }

                // step 5 : add document
                for (int i=0; i<addedDocuments.length(); i++) {
                    JSONObject document = addedDocuments.getJSONObject(i);
                    String type = document.getString("type");
                    File file = new File(
                            FileManager.getFileManager().getFolderPath() + "/"
                                    + this.corpus.getName() + "/"
                                    + type + "/"
                                    + document.getString("name")
                    );
                    httpRequestManager.addDocument(this.corpus.getCorpusId(), file, type, token);
                }

                // step 6 : add annotation
                for (int i=0; i<addedAnnotations.length(); i++) {
                    JSONObject annotation = addedAnnotations.getJSONObject(i);
                    String path = FileManager.getFileManager().getFolderPath() + "/"
                            + this.corpus.getName() + "/"
                            + Corpus.folderNameAnnotation + "/"
                            + annotation.getString("document") + "/"
                            + annotation.getString("name") + "/";

                    // get the audio file from the name
                    File audioFile = new File(path + annotation.getString("name"));

                    // get the json file from the name
                    String annotationName = annotation.getString("name");
                    String[] fileNamePart = annotationName.split("\\.");
                    String ext = "." + fileNamePart[fileNamePart.length - 1];
                    String trueFileName = annotationName.substring(0, annotationName.length() - ext.length());

                    File jsonFile  = new File(path + trueFileName + ".json");

                    int docId = annotation.getInt("docId");
                    if (docId == -1) {
                        docId = httpRequestManager.getDocIdByName(annotation.getString("document"), token);
                    }
                    httpRequestManager.addAnnotation(audioFile, jsonFile, docId, token, AuthenticationManager.getAuthenticationManager().getUserId());
                }

                // update corpus state
                JSONObject corpusStateResponse = httpRequestManager.getCorpusState(token, corpusId);
                JSONObject newServerState = new JSONObject(corpusStateResponse.get("body").toString());
                this.corpus.writeCorpusState(newServerState);

                // clean corpus modif
                this.corpus.resetCorpusModif();

                System.out.println("push done");

            } else {
                // you need to pull
                Platform.runLater(() -> SceneManager.getSceneManager().showNewModal(
                        "modals/AlertModalScene.fxml",
                        this.resources.getString("NidDePoule"),
                        this.resources.getString("NidDePoule")
                ));
            }

            LoadingManager.getLoadingManagerInstance().hideLoading(this.rootPane);
            this.loadingLabelPush.setVisible(false);
            System.out.println("Push Done");
        }).start();
    }

    /**
     * Push a new corpus on the server.
     */
    private void pushInit() {
        System.out.println("Start Push Init");
        this.loadingLabelPush.setVisible(true);
        LoadingManager.getLoadingManagerInstance().displayLoading(this.rootPane);

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
            LoadingManager.getLoadingManagerInstance().hideLoading(this.rootPane);
            System.out.println("Push Init Done");
        }).start();
    }


    /**
     * Fetch change from the server on your local.
     */
    public void pullCorpus() {
        if (!AuthenticationManager.getAuthenticationManager().isAuthenticated()) return;

        System.out.println("Start Pull");
        this.loadingLabelPull.setVisible(true);
        LoadingManager.getLoadingManagerInstance().displayLoading(this.rootPane);

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

                // on compare les deux corpus state
                JSONArray serveurDocs = serverCorpusState.getJSONArray("documents");
                JSONArray localDocs = localCorpusState.getJSONArray("documents");

                // get docs and annotations from server that do not exist on local
                JSONObject docsToDownload = existOnFirstAndNotOnSecond(serveurDocs, localDocs);
                fetchCorpusDiff(docsToDownload);

                // get docs and annotations from local that do not exist on the server anymore
                JSONObject docsToDelete = existOnFirstAndNotOnSecond(localDocs, serveurDocs);
                deleteCorpusDiff(docsToDelete);

                //pullThreadRunning = false;

                // set new corpus state
                FileManager fileManager = FileManager.getFileManager();
                File newCorpusState = new File(fileManager.getFolderPath() + "/" + this.corpus.getName() + "/corpus_state.json");
                fileManager.writeJSONFile(newCorpusState, serverCorpusState);

                // Reload corpus
                this.corpus.reload();
                Platform.runLater(() -> {
                    updateVideosList();
                    updateImagesList();
                    updateFieldAudioList();
                });


                this.loadingLabelPull.setVisible(false);
                LoadingManager.getLoadingManagerInstance().hideLoading(this.rootPane);
                System.out.println("Pull Done");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }).start();
    }

    public void goToVersionning(){
        SceneManager.getSceneManager().addArgument("corpus", this.corpus);
        SceneManager.getSceneManager().changeScene("VersionningScene.fxml");
    }

    public void goToModeration(){
        SceneManager.getSceneManager().addArgument("corpus", this.corpus);
        SceneManager.getSceneManager().changeScene("CorpusModerationScene.fxml");
    }


    /**
     * Get diffDocs from server and place them correctly in corpus file system.
     * @param diffDocs docs to fetch from server
     */
    private void fetchCorpusDiff(JSONObject diffDocs) {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Delete documents (and annotations) that exist locally but do not exist on the server anymore.
     * @param docsToDelete docs to delete locally
     */
    private void deleteCorpusDiff(JSONObject docsToDelete) {
        FileManager fileManager = FileManager.getFileManager();

        JSONArray documents = docsToDelete.getJSONArray("documents");
        JSONArray annotations = docsToDelete.getJSONArray("annotations");

        // we need to clear all annotations to delete some of them,
        // clear an annotation means use .dispose() on it's MediaPlayer.
        this.corpus.clearAnnotationsObjects();
        this.corpus.clearMediasObjects(); // same on document's files (FieldAudios, Images, Videos)

        // delete annotations
        for (int i=0; i<annotations.length(); i++) {
            String name = annotations.getJSONObject(i).getString("name");
            String docName = annotations.getJSONObject(i).getString("document");
            String path = fileManager.getFolderPath() + "/"
                    + this.corpus.getName() + "/"
                    + Corpus.folderNameAnnotation + "/"
                    + docName + "/"
                    + name;
            File folder = new File(path);
            fileManager.deleteFolder(folder);
        }

        // delete docs
        for (int i=0; i<documents.length(); i++) {
            // delete the doc
            String name = documents.getJSONObject(i).getString("name");
            String type = documents.getJSONObject(i).getString("type");
            int id = documents.getJSONObject(i).getInt("id");
            String path = fileManager.getFolderPath() + "/"
                    + this.corpus.getName() + "/"
                    + type + "/"
                    + name;
            File folder = new File(path);
            fileManager.deleteFile(folder);

            // delete it's annotations
            String annotationPath = fileManager.getFolderPath() + "/"
                    + this.corpus.getName() + "/"
                    + Corpus.folderNameAnnotation + "/"
                    + name;
            File annotationsfolder = new File(annotationPath);
            fileManager.deleteFolder(annotationsfolder);


            // When deleting a doc, we need to delete mentions of this doc in corpus_modif file.
            JSONObject corpusModif = this.corpus.getCorpusModif();

            // -> delete added annotation
            JSONObject added = corpusModif.getJSONObject("added");
            JSONArray addedAnnotations = added.getJSONArray("annotations");
            for (int j=0; j<addedAnnotations.length(); j++) {
                JSONObject annotation = addedAnnotations.getJSONObject(j);
                if (annotation.getString("document").equals(name)) {
                    addedAnnotations.remove(j);
                    added.put("annotations", addedAnnotations);
                    corpusModif.put("added", added);
                    System.out.println("delete " + name + " annotation from corpus modif");
                }
            }

            // -> delete deleted annotation
            JSONObject deleted = corpusModif.getJSONObject("deleted");
            JSONArray deletedAnnotations = deleted.getJSONArray("annotations");
            for (int j=0; j<deletedAnnotations.length(); j++) {
                JSONObject annotation = deletedAnnotations.getJSONObject(j);
                if (annotation.getInt("docId") == id ) {
                    deletedAnnotations.remove(j);
                    deleted.put("annotations", deletedAnnotations);
                    corpusModif.put("deleted", deleted);
                }
            }

            System.out.println(corpusModif);
            this.corpus.writeCorpusModif(corpusModif);
        }
    }


    /**
     * This function build and return an array called diffDocs.
     * The documents (and annotations) present in this array are the documents (and annotations) present in the first array parameter
     * but not present in the second array parameter.
     */
    private JSONObject existOnFirstAndNotOnSecond(JSONArray firstArray, JSONArray secondArray) {
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
        }
        */

        JSONObject diffDocs = new JSONObject();
        diffDocs.put("documents", new JSONArray());
        diffDocs.put("annotations", new JSONArray());


        // on regarde si les docs sur serveur existe en local
        for (int i=0; i < firstArray.length(); i++) {
            JSONObject doc = firstArray.getJSONObject(i);

            // si le doc existe pas, on l'ajoute a diffDocs
            boolean docExistOnLocal = existInDocumentsArray(doc, secondArray);

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
                JSONArray localAnnotations = findLocalAnnotations(doc, secondArray);
                for (int j=0; j<serverAnnotations.length(); j++) {
                    JSONObject a = serverAnnotations.getJSONObject(j);
                    if (!existInAnnotationsArray(a, localAnnotations)) {
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
        return diffDocs;
    }

    /**
     * Return true if document exist in documents array, else false.
     */
    private boolean existInDocumentsArray(JSONObject document, JSONArray documents) {
        int id = document.getInt("docId");
        String name = document.getString("name");

        for (int i=0; i < documents.length(); i++) {
            JSONObject doc = documents.getJSONObject(i);
            if (doc.getInt("docId") == id && doc.getString("name").equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return true if annotation exist in annotations array, else false.
     */
    private boolean existInAnnotationsArray(JSONObject annotation, JSONArray annotations) {
        int id = annotation.getInt("annotationId");
        String name = annotation.getString("name");

        for (int i=0; i<annotations.length(); i++) {
            JSONObject a = annotations.getJSONObject(i);
            if (a.getInt("annotationId") == id && a.getString("name").equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the locals annotations from the local document corresponding to the document from server.
     * Return an empty array if the document is not found.
     * It's used to compare annotations from local and server of a document.
     */
    private JSONArray findLocalAnnotations(JSONObject docFromServer, JSONArray localDocs) {
        int id = docFromServer.getInt("docId");
        String name = docFromServer.getString("name");

        for (int i=0; i<localDocs.length(); i++) {
            JSONObject localDoc = localDocs.getJSONObject(i);
            if (localDoc.getString("name").equals(name) && localDoc.getInt("docId") == id) {
                return localDoc.getJSONArray("annotations");
            }
        }
        return new JSONArray();
    }

    private void renameDoc(Media doc, Corpus corpus) {
        String[] docNamePart = doc.getName().split("\\.");
        String ext = "." + docNamePart[docNamePart.length - 1];
        String lastName = doc.getName().substring(0, doc.getName().length() - ext.length());
        String renameRessource = "";
        if(doc.getTypeDocument() == TypeDocument.FieldAudio) {
            renameRessource = resources.getString("RenameAudio");
        } else if (doc.getTypeDocument() == TypeDocument.Images) {
            renameRessource = resources.getString("RenameImage");
        } else if (doc.getTypeDocument() == TypeDocument.Videos) {
            renameRessource = resources.getString("RenameVideo");
        }
        String newName = SceneManager.getSceneManager().showNewModal("modals/AudioDescriptionEditScene.fxml", lastName, renameRessource);
        if(!newName.equals(lastName) && !newName.isEmpty()) {
            corpus.renameDocument(doc, newName + ext);
            if(doc.getTypeDocument() == TypeDocument.FieldAudio) {
                this.updateFieldAudioList();
            } else if (doc.getTypeDocument() == TypeDocument.Images) {
                this.updateImagesList();
            } else if (doc.getTypeDocument() == TypeDocument.Videos) {
                this.updateVideosList();
            }
        }
    }
}