package com.tetkole.tetkole.utils;

import javax.sound.sampled.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class RecordManager {
    private final String os;
    private final String folderPath;
    private boolean isRecording = false;
    private TargetDataLine targetLine;
    Thread audioRecordThread;

    public RecordManager() {
        this.os = System.getProperty("os.name").toLowerCase();
        String userName = System.getProperty("user.name");
        if (this.os.contains("nux") || this.os.contains("mac")){
            this.folderPath = "/home/" + userName + "/TetKole";
        }else {
            this.folderPath = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\TetKole";
        }
        try {
            Files.createDirectories(Path.of(this.folderPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void startRecording(String fileName){
        if(!isRecording) {
            this.isRecording = true;
            System.out.println("Record started");
            try {
                AudioFormat audioFormat = getAudioFormat();
                DataLine.Info dataInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

                if (!AudioSystem.isLineSupported(dataInfo)){
                    System.out.println("Not supported");
                }else {
                    targetLine = (TargetDataLine)AudioSystem.getLine(dataInfo);
                    targetLine.open(audioFormat);
                    targetLine.start();

                    audioRecordThread = new Thread(() -> {
                        AudioInputStream recordStream = new AudioInputStream(targetLine);
                        try {
                            AudioSystem.write(recordStream, AudioFileFormat.Type.WAVE, this.getWavOutputFile(fileName));
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

    public void stopRecording(){
        if(this.isRecording) {
            this.isRecording = false;
            System.out.println("Record stopped");
            audioRecordThread.stop();
            targetLine.stop();
            targetLine.close();
        }
    }

    public AudioFormat getAudioFormat(){
        float sampleRate = 16000;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;

        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public File getWavOutputFile(String fileName){
        Long localDateEpoch =  LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        if (this.os.contains("nux") || this.os.contains("mac")){
            new File(this.folderPath+"/"+fileName);
            return new File(this.folderPath +"/"+fileName+ "/record-"+localDateEpoch+".wav");
        }else{
            //create the folder if it doesn't exist
            new File(this.folderPath+"\\"+fileName);
            return new File(this.folderPath + "\\"+fileName+"/record-"+localDateEpoch+".wa");
        }
    }
}
