package com.tetkole.tetkole.utils.models;

import java.io.File;

public class Annotation {
    private File file;

    private double start;

    private double end;

    public Annotation(double start, double end) {
        //TODO Annotation Constructor
        this.start = start;
        this.end = end;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
    }
}
