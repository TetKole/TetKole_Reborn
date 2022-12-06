package com.tetkole.tetkole.utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class RecordManager {
    private boolean isRecording = false;
    private TargetDataLine targetLine;
    Thread audioRecordThread;

    public RecordManager() { }

    public boolean isRecording() {
        return isRecording;
    }

    // we will delete this method once corpus is implemented in image and video
    public void startRecording(String fileName, String recordName) {
        startRecording(fileName, recordName, "");
    }

    public void startRecording(String fileName, String recordName, String corpusPath) {
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
                            AudioSystem.write(recordStream, AudioFileFormat.Type.WAVE, this.getWavOutputFile(corpusPath, recordName));
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

    public void stopRecording() {
        if (this.isRecording) {
            this.isRecording = false;
            System.out.println("Record stopped");
            audioRecordThread.stop();
            targetLine.stop();
            targetLine.close();
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
