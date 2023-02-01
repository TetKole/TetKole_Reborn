package com.tetkole.tetkole.utils.models;

import java.io.File;

public class FieldAudio extends Media {

    private String description = "";

    public FieldAudio(File file, String description) {
        super(file);
        this.description = description;
    }

    public FieldAudio(File file) {
        this(file, "");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
