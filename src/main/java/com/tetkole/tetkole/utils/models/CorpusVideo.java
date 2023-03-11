package com.tetkole.tetkole.utils.models;

import java.io.File;

public class CorpusVideo extends Media {

    public CorpusVideo(File file, Corpus corpus) {
        super(file, corpus);
        this.setTypeDocument(TypeDocument.Videos);
    }

}
