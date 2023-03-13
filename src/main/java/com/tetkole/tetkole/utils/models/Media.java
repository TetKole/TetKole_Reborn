package com.tetkole.tetkole.utils.models;

import com.tetkole.tetkole.utils.FileManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class Media {

    private File file;
    private List<Annotation> annotations;
    private Corpus corpus;
    private TypeDocument typeDocument;

    public Media(File file, Corpus corpus) {
        this.file = file;
        this.annotations = new ArrayList<>();
        this.corpus = corpus;
    }

    public String getName() {
        return this.file.getName();
    }

    public File getFile() {
        return this.file;
    }

    public List<Annotation> getAnnotations() {
        return this.annotations;
    }

    public void setTypeDocument(TypeDocument typeDocument) {
        this.typeDocument = typeDocument;
    }

    public void addAnnotation(Annotation annotation) {
        this.annotations.add(annotation);

        if (this.corpus.getCorpusState() != null) {
            // corpus_modif
            JSONObject modif = new JSONObject();
            modif.put("document", this.getName());
            modif.put("name", annotation.getName());
            modif.put("docId", this.getId());
            this.corpus.getCorpusModif().getJSONObject("added").getJSONArray("annotations").put(modif);
            this.corpus.writeCorpusModif();
        }
    }

    /**
     * This method should only be used when loading a corpus
     */
    public void addAnnotationInit(Annotation annotation) {
        this.annotations.add(annotation);
    }

    public void deleteAnnotation(Annotation annotation) {
        for (Annotation a : this.annotations) {
            if (a.equals(annotation)) {

                if (this.corpus.getCorpusState() != null) {
                    deleteFromCorpusModif(annotation);
                }

                annotation.delete();
                this.annotations.remove(annotation);
                break;
            }
        }
    }

    private void deleteFromCorpusModif(Annotation annotation) {
        // corpus_modif
        JSONObject corpus_modif = this.corpus.getCorpusModif();

        // first check if we didn't add the annotation inside the corpus_modif
        // if yes -> just delete this annotation in the added section
        boolean weAddedThis = false;
        int index = -1;
        JSONArray array = corpus_modif.getJSONObject("added").getJSONArray("annotations");

        // search if the annotation is in the added section
        for (int i=0; i<array.length(); i++) {
            if (array.getJSONObject(i).get("name").equals(annotation.getName())) {
                weAddedThis = true;
                index = i;
                break;
            }
        }

        if (weAddedThis) {
            array.remove(index);
        }
        else
        {
            // else -> add the annotation in the deleted section

            // get annotation's id
            int id = -1;

            JSONObject corpus_state = this.corpus.getCorpusState();
            JSONArray documents = corpus_state.getJSONArray("documents");

            for (int i=0; i<documents.length(); i++) {
                JSONObject document = documents.getJSONObject(i);
                JSONArray annotations = document.getJSONArray("annotations");

                for (int j=0; j<annotations.length(); j++) {
                    JSONObject tmpAnnotation = annotations.getJSONObject(j);
                    if (tmpAnnotation.getString("name").equals(annotation.getName())) {
                        id = tmpAnnotation.getInt("annotationId");
                        break;
                    }
                }
            }

            if (id == -1) {
                System.err.println("this annotations does not have an ID -- deleteFromCorpusModif() in Media.java");
            }

            JSONObject modif = new JSONObject();
            modif.put("name", annotation.getName());
            modif.put("id", id);
            modif.put("docName", this.getName());
            modif.put("docId", this.getId());
            corpus_modif.getJSONObject("deleted").getJSONArray("annotations").put(modif);
        }

        this.corpus.writeCorpusModif();
    }

    public int getId() {
        JSONObject corpus_state = this.corpus.getCorpusState();
        JSONArray documents = corpus_state.getJSONArray("documents");

        for (int i=0; i< documents.length(); i++) {
            JSONObject document = documents.getJSONObject(i);
            if (document.getString("name").equals(this.getName())) {
                return document.getInt("docId");
            }
        }

        JSONObject corpus_modif = this.corpus.getCorpusModif();
        documents = corpus_modif.getJSONObject("updated").getJSONArray("documents");
        for (int i=0; i< documents.length(); i++) {
            JSONObject document = documents.getJSONObject(i);
            if (document.getString("newName").equals(this.getName())) {
                return document.getInt("id");
            }
        }

        return -1;
    }

    public void clearAnnotations() {
        for (Annotation annotation: annotations) {
            annotation.getMediaPlayer().dispose();
        }
    }

    public void clearFile() {
        this.file = null;
    }

    public void renameMedia(String newName, String corpusName) {
        this.clearAnnotations();
        String lastName = this.getName();
        File file = this.getFile();
        FileManager.getFileManager().renameAnnotEcrite(corpusName, this.getName(), this.typeDocument, newName);
        this.file = FileManager.getFileManager().renameFile(file, newName);
        FileManager.getFileManager().renameDirectoryDocument(lastName, corpusName, newName);
        for (Annotation annotation: this.getAnnotations()
        ) {
            annotation.renameDocName(newName);
        }
    }
}
