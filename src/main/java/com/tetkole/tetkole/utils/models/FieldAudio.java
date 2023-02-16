package com.tetkole.tetkole.utils.models;

import java.io.File;

public class FieldAudio extends Media {

    private String description = "";

    public FieldAudio(File file, String description, Corpus corpus) {
        super(file, corpus);
        this.description = description;
    }

    public FieldAudio(File file, Corpus corpus) {
        this(file, "", corpus);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
