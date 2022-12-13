package com.tetkole.tetkole.utils.models;

import com.tetkole.tetkole.utils.FileManager;

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

    public File getFile() {
        return this.file;
    }

    public void delete(String fieldAudioName, String corpusName) {
        String annotationDirPath = FileManager.getFileManager().getFolderPath() + "/" + corpusName + "/" + Corpus.folderNameAnnotation + "/" + fieldAudioName + "/" + this.getName();
        FileManager.getFileManager().deleteFolder(new File(annotationDirPath));
    }
}
