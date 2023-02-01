package com.tetkole.tetkole.utils;


import com.tetkole.tetkole.utils.models.Annotation;
import com.tetkole.tetkole.utils.models.Media;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class RecordManager {
    private boolean isRecording = false;
    private TargetDataLine targetLine;
    private Thread audioRecordThread;

    private String fileName;
    private String recordName;
    private String corpusPath;
    private double rightBorderValue;
    private double leftBorderValue;
    private File file;

    public RecordManager() { }

    public boolean isRecording() {
        return isRecording;
    }

    public void startRecording(String fileName, String recordName, String corpusPath, double rightBorderValue, double leftBorderValue) {
        this.fileName = fileName;
        this.recordName = recordName;
        this.corpusPath = corpusPath;
        this.rightBorderValue = rightBorderValue;
        this.leftBorderValue = leftBorderValue;

        if (!isRecording) {
            this.isRecording = true;
            System.out.println("Record started");
            try {
                AudioFormat audioFormat = getAudioFormat();
                DataLine.Info dataInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

                if (!AudioSystem.isLineSupported(dataInfo)) {
                    System.out.println("Not supported");
                } else {
                    targetLine = (TargetDataLine)AudioSystem.getLine(dataInfo);
                    targetLine.open(audioFormat);
                    targetLine.start();

                    audioRecordThread = new Thread(() -> {
                        AudioInputStream recordStream = new AudioInputStream(targetLine);
                        try {
                            this.file = this.getWavOutputFile(corpusPath, recordName);
                            AudioSystem.write(recordStream, AudioFileFormat.Type.WAVE, this.file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    audioRecordThread.start();
                }
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopRecording(Media media) {
        if (this.isRecording) {
            this.isRecording = false;
            System.out.println("Record stopped");
            audioRecordThread.stop();
            targetLine.stop();
            targetLine.close();

            Map<String, Object> fileMap = Map.of(
                    "fileName", this.fileName,
                    "recordName", this.recordName,
                    "start", this.leftBorderValue,
                    "end", this.rightBorderValue
            );

            // JSON file is created here !
            FileManager.getFileManager().createJSONFile(
                    this.recordName,
                    fileMap,
                    this.corpusPath + '/' + this.recordName
            );

            media.addAnnotation(new Annotation(this.file, this.leftBorderValue, this.rightBorderValue));
        }
    }

    public AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;

        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    /**
     * Create a folder named as the fileName
     * then create an audio file inside and return it
     */
    public File getWavOutputFile(String corpusPath, String recordName) {
        FileManager.getFileManager().createFolder(corpusPath, recordName);
        return FileManager.getFileManager().createFile(corpusPath + "/" + recordName, recordName);
    }
}
