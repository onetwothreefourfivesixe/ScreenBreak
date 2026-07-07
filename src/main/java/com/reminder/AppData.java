package com.reminder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class AppData {
    private final String appName = "ScreenBreak";
    private String userFolderPath;
    private String userFullPath;

    public AppData() {
        // Set the default user folder path to the user's home directory
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            this.userFolderPath = System.getenv("AppData");
        } else if (osName.contains("mac")) {
            this.userFolderPath = System.getProperty("user.home") + "/Library/Application Support";
        } else {
            this.userFolderPath = System.getProperty("user.home");
        }
        this.userFullPath = this.userFolderPath + "/" + this.appName;

        try {
            if (!Files.exists(java.nio.file.Paths.get(this.userFullPath))) {
                Files.createDirectories(java.nio.file.Paths.get(this.userFullPath));
                Files.createDirectories(java.nio.file.Paths.get(this.userFullPath + "/sounds"));
                Files.createFile(java.nio.file.Paths.get(this.userFullPath + "/config.properties"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Properties getProperties() {
        try {
            FileReader fileReader = new FileReader(this.userFullPath + "/config.properties");
            Properties p = new Properties();
            p.load(fileReader);
            return p;
        } catch (Exception e) {
            e.printStackTrace();
            return new Properties();
        }
    }

    public void saveProperties(Properties p) {
        try {
            java.io.FileWriter writer = new java.io.FileWriter(this.userFullPath + "/config.properties");
            p.store(writer, "ScreenBreak Configuration");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File loadSoundFile() {
        try {
            return Files.list(Paths.get(userFullPath + "/sounds/"))
                    .filter(Files::isRegularFile)
                    .findFirst()
                    .map(java.nio.file.Path::toFile)
                    .orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void uploadSoundFile(File soundFile) {
        try {
            String soundFileFolderPath = this.userFullPath + "/sounds";
            Files.walk(Paths.get(soundFileFolderPath))
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    });
            java.nio.file.Path targetPath = Paths.get(soundFileFolderPath).resolve(soundFile.getName());
            Files.copy(soundFile.toPath(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAppName() {
        return appName;
    }

    public String getUserFolderPath() {
        return userFolderPath;
    }

    public String getUserFullPath() {
        return userFullPath;
    }
}
