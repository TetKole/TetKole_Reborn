package com.tetkole.tetkole.utils;

import org.json.JSONObject;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileManager {
    private static FileManager fileManagerInstance;

    private final String os;
    private final String folderPath;

    // true if on Windows, false if other
    private final boolean isWindows;

    private FileManager() {
        this.os = System.getProperty("os.name").toLowerCase();

        String userName = System.getProperty("user.name");

        if (this.os.contains("nux") || this.os.contains("mac")) {
            this.folderPath = "/home/" + userName + "/TetKole";
            this.isWindows = false;
        } else {
            this.folderPath = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\TetKole";
            this.isWindows = true;
        }
    }

    /**
     * Setter of the singleton
     */
    public static void setFileManager() {
        fileManagerInstance = new FileManager();
    }

    /**
     * Getter of the singleton
     */
    public static FileManager getFileManager() {
        return fileManagerInstance;
    }

    /**
     * Get the folderPath
     */
    public String getFolderPath() {
        return this.folderPath;
    }

    /**
     * Return true if on Windows, false if else
     */
    public boolean getIsWindows() {
        return this.isWindows;
    }


    /**
     * Create a file into Tetkole folder
     */
    public File createFile(String fileName) {
        return new File(this.folderPath + "/" + fileName);
    }

    /**
     * Create a file into a corpus
     * @param path ALL THE PARAMETER PATH MUST BE RELATIVE
     *             should be corpus path
     *             example :
     *                  for corpus named "lousianne"
     *                  create a file in lousianne corpsu directory
     *                  call createNewFile("lousianne", "myNewFileName")
     */
    public File createFile(String path, String fileName) {
        return new File(this.folderPath + "/" + path + "/" + fileName);
    }



    /**
     * Create a folder into Tetkole folder
     */
    public void createFolder(String folderName) {
        new File(this.folderPath + "/" + folderName).mkdir();
    }

    /**
     * Create a folder into a corpus
     * @param path ALL THE PARAMETER PATH MUST BE RELATIVE
     *             should be corpus path
     *             example :
     *                  for corpus named "images"
     *                  create a folder in lousianne corpus folder
     *                  call createFolder("lousianne", "images")
     */
    public void createFolder(String path, String folderName) {
        new File(this.folderPath + "/" + path + "/" + folderName).mkdir();
    }


    /**
     * Copy file to destPath
     * @param destPath ALL THE PARAMETER PATH MUST BE RELATIVE
     */
    public File copyFile(File fileToCopy, String destPath) {
        try {
            destPath = this.folderPath + "/" + destPath + "/" + fileToCopy.getName();
            Path newFilePath = Files.copy(fileToCopy.toPath(), (new File(destPath).toPath()), StandardCopyOption.REPLACE_EXISTING);
            return newFilePath.toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Create a json file
     * this method should be used only when adding a new annotation
     */
    public void createJSONFile(String fileName, String recordName, Double start, Double end, String corpusPath) {
        JSONObject json = new JSONObject();
        json.put("fileName", fileName);
        json.put("recordName", recordName);
        json.put("start", start);
        json.put("end", end);

        FileManager.getFileManager().createFile(fileName, recordName);

        try {
            FileWriter file = new FileWriter(FileManager.getFileManager().getFolderPath() + corpusPath + "/" + recordName + "/" + recordName + ".json");
            file.write(json.toString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read json file's content
     * this method should be used only to create Annotation object from json file
     */
    public JSONObject readJSONFile(File file) {
        try {
            String data = Files.readString(Path.of(file.getPath()));
            return new JSONObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Rename file's name to newName.
     */
    public File renameFile(File file, String newName) {
        file.renameTo(new File(file.getParentFile() + "/" + newName));
        return new File(file.getParentFile() + "/" + newName);
    }

}
