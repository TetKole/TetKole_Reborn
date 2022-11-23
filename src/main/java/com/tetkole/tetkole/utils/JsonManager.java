package com.tetkole.tetkole.utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonManager {
    private  String fileName;
        public JsonManager() {
        }

        public void saveJson(String fileName, String recordName, Long start, Long end) {
            String message;
            JSONObject json = new JSONObject();
            json.put("fileName", fileName);
            json.put("recordName", recordName);
            json.put("start", start);
            json.put("end", end);
            message = json.toString();

            System.out.println(message);
        }
}
