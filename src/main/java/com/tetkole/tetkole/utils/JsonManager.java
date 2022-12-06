package com.tetkole.tetkole.utils;

import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;

public class JsonManager {

    public JsonManager() { }

    public void saveJson(String fileName, String recordName, Double start, Double end, String corpusPath) {
        JSONObject json = createObject(fileName, recordName, start, end);

        FileManager.getFileManager().createFile(fileName, recordName);

        try {
            FileWriter file = new FileWriter(FileManager.getFileManager().getFolderPath() + corpusPath + "/" + recordName + "/" + recordName + ".json");
            file.write(json.toString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject createObject(String fileName, String recordName, Double start, Double end) {
        JSONObject json = new JSONObject();
        json.put("fileName", fileName);
        json.put("recordName", recordName);
        json.put("start", start);
        json.put("end", end);
        return json;
    }
}
