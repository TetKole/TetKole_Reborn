package com.tetkole.tetkole.utils;

import org.json.JSONObject;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.io.File;

public class JsonManager {
    private final  String os;
    private final String folderPath;


    public JsonManager() {
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

        public void saveJson(String fileName, String recordName, Double start, Double end) {
            JSONObject json = createObject(fileName,recordName,start,end);
            if (this.os.contains("nux") || this.os.contains("mac")){
                new File(this.folderPath+"/"+fileName);
                try {
                    FileWriter file = new FileWriter(this.folderPath+"/"+fileName+"/"+recordName+".json");
                    file.write(json.toString());
                    file.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }else{
                //create the folder if it doesn't exist
                new File(this.folderPath+"\\"+fileName).mkdir();
                try {
                    FileWriter file = new FileWriter(this.folderPath+"\\"+fileName+"\\"+recordName+".json");
                    file.write(json.toString());
                    file.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        public JSONObject createObject(String fileName, String recordName, Double start, Double end) {
            String message;
            JSONObject json = new JSONObject();
            json.put("fileName", fileName);
            json.put("recordName", recordName);
            json.put("start", start);
            json.put("end", end);
            message = json.toString();
            System.out.println(message);
            return json;
        }
}
