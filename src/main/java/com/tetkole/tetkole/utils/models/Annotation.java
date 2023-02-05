package com.tetkole.tetkole.utils.models;

import com.tetkole.tetkole.utils.FileManager;

import java.io.File;

public class Annotation {
    private File file;

    private final double start;

    private final double end;

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

    public double getStart() {
        return start;
    }

    public double getEnd() {
        return end;
    }

    public File getJsonFile() {
        String name = this.file.getName();
        return new File(this.file.getParentFile() + "/" + name.substring(0, name.length() - 3) + "json");
    }

    public String getDocumentName() {
        return this.file.getParentFile().getParentFile().getName();
    }
}
