package com.tetkole.tetkole.utils;

import com.tetkole.tetkole.utils.models.Corpus;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

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
        new File(this.folderPath).mkdir();
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
    public void createJSONFile(String fileName, Map<String, Object> map, String relativeLocation) {
        JSONObject json = new JSONObject();

        json.put("fileName", fileName);
        map.forEach(json::put);

        FileManager.getFileManager().createFile(relativeLocation, fileName.split("\\.")[0]);
        String fileNameWithoutExt = fileName.split("\\.")[0];
        try {
            FileWriter file = new FileWriter(getFolderPath() + relativeLocation + "/" + fileNameWithoutExt + ".json");
            file.write(json.toString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read json file's content
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
     * Write content in json file
     */
    public void writeJSONFile(File jsonFile, JSONObject jsonContent) {
        try {
            // Write in corpus_state file
            FileOutputStream fos = new FileOutputStream(jsonFile);
            byte[] data = jsonContent.toString().getBytes(StandardCharsets.UTF_8);
            fos.flush();
            fos.write(data);
            fos.close();
        } catch (Exception IOException) {
            System.err.println("Could not write in " + jsonFile.getName());
        }
    }

    /**
     * Rename file's name to newName.
     */
    public File renameFile(File file, String newName) {
        file.renameTo(new File(file.getParentFile() + "/" + newName));
        return new File(file.getParentFile() + "/" + newName);
    }

    /**
     * Delete file
     */
    public void deleteFile(File fileToDelete) {
        if (!fileToDelete.delete()) {
            System.err.println("\nCannot delete file " + fileToDelete.getName());
        }
    }

    public void deleteFolder(File folderToDelete) {
        File[] allContents = folderToDelete.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                this.deleteFile(file);
            }
        }
        if (!folderToDelete.delete()) {
            System.err.println("\nCannot delete directory " + folderToDelete.getName());
        }
    }

    // Download file from url
    private void downloadFileFromURL(String urlStr, String path) throws IOException {
        URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(folderPath + "/" + path);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

    public void downloadAnnotation(String corpusName, String docName, String fileName) throws IOException {
        String path = corpusName + "/" + Corpus.folderNameAnnotation + "/" + docName;
        FileManager.getFileManager().createFolder(path, fileName);

        path = path + "/" + fileName;

        // Download doc file
        String pathFile = path + "/" + fileName;
        downloadFileFromURL(HttpRequestManager.servURL + "/" + pathFile, pathFile);

        // Download json file
        String pathJson = path + "/" + fileName.replace(".wav", ".json");
        downloadFileFromURL(HttpRequestManager.servURL + "/" + pathJson, pathJson);
    }

    public void downloadDocument(String typeDoc, String corpusName, String fileName) throws IOException {
        String path = corpusName + "/" + typeDoc + "/" + fileName;
        downloadFileFromURL(HttpRequestManager.servURL + "/" + path, path);
        FileManager.getFileManager().createFolder(corpusName + "/" + Corpus.folderNameAnnotation, fileName);
    }

    public void createCorpusModifFile(String corpusName) {
        File file = new File(this.folderPath + "/" + corpusName + "/corpus_modif.json");
        JSONObject corpus_modif = new JSONObject();
        corpus_modif.accumulate("added", new JSONObject());
        corpus_modif.accumulate("deleted", new JSONObject());

        corpus_modif.getJSONObject("added").put("documents", new JSONArray());
        corpus_modif.getJSONObject("added").put("annotations", new JSONArray());

        corpus_modif.getJSONObject("deleted").put("documents", new JSONArray());
        corpus_modif.getJSONObject("deleted").put("annotations", new JSONArray());

        writeJSONFile(file, corpus_modif);
    }
}
