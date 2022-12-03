package com.tetkole.tetkole.utils.models;

import java.io.File;
import java.util.List;

public class FieldAudio {

    private File file;

    private List<Annotation> annotations;

    public FieldAudio(String Path) {

    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }
}
