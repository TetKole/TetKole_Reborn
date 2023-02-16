package com.tetkole.tetkole.utils.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class Media {

    private File file;
    private List<Annotation> annotations;
    private Corpus corpus;

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

    public void addAnnotation(Annotation annotation) {
        this.annotations.add(annotation);

        // corpus_modif
        JSONObject modif = new JSONObject();
        modif.put("document", this.getName());
        modif.put("name", annotation.getName());
        this.corpus.getCorpusModif().getJSONObject("added").getJSONArray("annotations").put(modif);
        this.corpus.writeCorpusModif();
    }

    public void deleteAnnotation(Annotation annotation) {
        for (Annotation a : this.annotations) {
            if (a.equals(annotation)) {

                deleteFromCorpusModif(annotation);

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
            JSONObject modif = new JSONObject();
            modif.put("document", this.getName());
            modif.put("name", annotation.getName());
            corpus_modif.getJSONObject("deleted").getJSONArray("annotations").put(modif);
        }

        this.corpus.writeCorpusModif();
    }
}
