package com.tetkole.tetkole.utils.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FieldAudio {

    private File file;

    private List<Annotation> annotations;

    public FieldAudio(File file) {
        this.file = file;
        this.annotations = new ArrayList<>();
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
    }
}
