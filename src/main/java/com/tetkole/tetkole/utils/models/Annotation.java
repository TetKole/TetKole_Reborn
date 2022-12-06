package com.tetkole.tetkole.utils.models;

import java.io.File;

public class Annotation {
    private File file;

    private double start;

    private double end;

    public Annotation(File file, double start, double end) {
        this.file = file;
        this.start = start;
        this.end = end;
    }

    public String getName() {
        return this.file.getName();
    }
}
