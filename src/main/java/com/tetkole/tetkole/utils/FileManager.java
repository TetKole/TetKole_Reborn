package com.tetkole.tetkole.utils;

import com.tetkole.tetkole.utils.models.Corpus;
import com.tetkole.tetkole.utils.models.TypeDocument;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
        initConfigJSON();
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
     * Copy file to destPath
     * @param destPath ALL THE PARAMETER PATH MUST BE RELATIVE
     */
    public File absoluteCopyFile(File fileToCopy, String destPath) {
        try {
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

        String[] fileNamePart = fileName.split("\\.");
        String ext = "." + fileNamePart[fileNamePart.length - 1];
        String trueFileName = fileName.substring(0, fileName.length() - ext.length());

        FileManager.getFileManager().createFile(relativeLocation, trueFileName);
        String fileNameWithoutExt = trueFileName;
        try {
            FileWriter file = new FileWriter(getFolderPath() + relativeLocation + "/" + fileNameWithoutExt + ".json");
            file.write(json.toString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initConfigJSON() {
        File configJSON = new File(this.folderPath + "/config.json");
        if(configJSON.exists()) {
            JSONObject configData = readJSONFile(configJSON);
            StaticEnvVariable.zoomRange = configData.getInt("amplitude");
        } else {
            JSONObject json = new JSONObject();
            json.put("amplitude", 200);
            try {
                FileWriter file = new FileWriter(this.folderPath + "/config.json");
                file.write(json.toString());
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            StaticEnvVariable.zoomRange = 200;
        }
    }

    public void setAudioZoomRange(int zoomRange) {
        File configJSON = new File(this.folderPath + "/config.json");
        JSONObject json = new JSONObject();
        json.put("amplitude", zoomRange);
        try {
            FileWriter file = new FileWriter(this.folderPath + "/config.json");
            file.write(json.toString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StaticEnvVariable.zoomRange = zoomRange;
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
        String separator = this.os.contains("nux") || this.os.contains("mac") ? "/" : "\\";

        System.gc();

        file.renameTo(new File(file.getParentFile() + separator + newName));
        return new File(file.getParentFile() + separator + newName);
    }

    /**
     * Rename file's name to newName.
     */
    public void renameAnnotEcrite(String corpusName, String docName, TypeDocument typeDocument, String newName) {
        String separator = this.os.contains("nux") || this.os.contains("mac") ? "/" : "\\";

        System.gc();

        String[] fileNamePart = docName.split("\\.");
        String ext = "." + fileNamePart[fileNamePart.length - 1];
        String trueFileName = docName.substring(0, docName.length() - ext.length());

        String trueNewFileName = newName.substring(0, newName.length() - ext.length());

        File fileAnnot = new File(this.folderPath + separator + corpusName + separator + typeDocument + separator + trueFileName + ".json");

        fileAnnot.renameTo(new File(fileAnnot.getParentFile() + separator + trueNewFileName + ".json"));
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
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    this.deleteFile(file);
                }
            }
        }
        if (!folderToDelete.delete()) {
            System.err.println("\nCannot delete directory " + folderToDelete.getName());
        }
    }

    // Download file from url
    private void downloadFileFromURL(String urlStr, String path) throws IOException, URISyntaxException {
        URL url = new URL(urlStr);
        URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        url = uri.toURL();
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(folderPath + "/" + path);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

    public void downloadAnnotation(String corpusName, String docName, String fileName) throws IOException, URISyntaxException {
        String path = corpusName + "/" + Corpus.folderNameAnnotation + "/" + docName;
        FileManager.getFileManager().createFolder(path, fileName);

        path = path + "/" + fileName;

        // Download doc file
        String pathFile = path + "/" + fileName;
        downloadFileFromURL(HttpRequestManager.servURL + "/corpus/" + pathFile, pathFile);

        // Download json file
        String pathJson = path + "/" + fileName.replace(".wav", ".json");
        downloadFileFromURL(HttpRequestManager.servURL + "/corpus/" + pathJson, pathJson);
    }

    public void downloadDocument(String typeDoc, String corpusName, String fileName) throws IOException, URISyntaxException {
        String path = corpusName + "/" + typeDoc + "/" + fileName;
        downloadFileFromURL(HttpRequestManager.servURL + "/corpus/" + path, path);
        FileManager.getFileManager().createFolder(corpusName + "/" + Corpus.folderNameAnnotation, fileName);
    }

    public boolean downloadVersion(String corpusName, Integer version){
        String pathServer = corpusName + "/" + corpusName + "-" + version + ".zip";
        String pathLocal = corpusName + "/Versions/" + corpusName + "-" + version + ".zip";
        try {
            downloadFileFromURL(HttpRequestManager.servURL + "/versions/" + pathServer, pathLocal);
            JSONObject response = null;
        } catch (IOException | URISyntaxException e) {
            return false;
        }
        return true;
    }

    public JSONObject createCorpusModifFile(String corpusName) {
        File file = new File(this.folderPath + "/" + corpusName + "/corpus_modif.json");
        JSONObject corpus_modif = new JSONObject();
        corpus_modif.accumulate("added", new JSONObject());
        corpus_modif.accumulate("deleted", new JSONObject());
        corpus_modif.accumulate("updated", new JSONObject());

        corpus_modif.getJSONObject("added").put("documents", new JSONArray());
        corpus_modif.getJSONObject("added").put("annotations", new JSONArray());

        corpus_modif.getJSONObject("deleted").put("documents", new JSONArray());
        corpus_modif.getJSONObject("deleted").put("annotations", new JSONArray());

        corpus_modif.getJSONObject("updated").put("documents", new JSONArray());
        corpus_modif.getJSONObject("updated").put("annotations", new JSONArray());

        writeJSONFile(file, corpus_modif);
        return corpus_modif;
    }

    public void renameDirectoryDocument(String docName, String corpusName, String newName) {
        String separator = this.os.contains("nux") || this.os.contains("mac") ? "/" : "\\";
        String destPath = this.folderPath + separator + corpusName + separator + TypeDocument.Annotations + separator + newName;
        File source = new File(this.folderPath + separator + corpusName + separator + TypeDocument.Annotations + separator + docName);
        File dest = new File(destPath);
        dest.mkdir();
        File[] files = source.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File destAnnot = new File(destPath + separator + files[i].getName());
                destAnnot.mkdir();
                File[] filesAnnot = files[i].listFiles();
                for (int j = 0; j < filesAnnot.length; j++) {
                    this.absoluteCopyFile(filesAnnot[j], destAnnot.getPath() + separator + filesAnnot[j].getName());
                }
            }
        }
        this.deleteFolder(source);

    }

    public File getAnnotationFile(String annotationName, String corpusName, String fieldAudioName) {
        String separator = this.os.contains("nux") || this.os.contains("mac") ? "/" : "\\";
        return new File(this.folderPath + separator + corpusName + separator + TypeDocument.Annotations + separator + fieldAudioName + separator + annotationName + separator + annotationName );
    }

    public void renameDirectoryAnnotation(String annotationName, String corpusName, String fieldAudioName, String newName) {
        String separator = this.os.contains("nux") || this.os.contains("mac") ? "/" : "\\";
        String destPath = this.folderPath + separator + corpusName + separator + TypeDocument.Annotations + separator + fieldAudioName + separator + newName;
        File source = new File(this.folderPath + separator + corpusName + separator + TypeDocument.Annotations + separator + fieldAudioName + separator + annotationName);
        File dest = new File(destPath);
        dest.mkdir();
        File[] files = source.listFiles();
        if (files != null) {
            for (int j = 0; j < files.length; j++) {
                this.absoluteCopyFile(files[j], dest.getPath() + separator + files[j].getName());
            }
        }
        this.deleteFolder(source);
    }
}
