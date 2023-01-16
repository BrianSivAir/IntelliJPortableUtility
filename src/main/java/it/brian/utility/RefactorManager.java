package it.brian.utility;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RefactorManager {
    private static final Logger logger = LogManager.getLogger(RefactorManager.class);
    private static final String token = "$TOKEN$";
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private char currentDrive;

    public RefactorManager(char currentDrive) {
        this.currentDrive = currentDrive;
    }

    public void loadSettings(String path) {
        //GIT
        logger.info("Starting refactoring: git.xml");
        Path git = Paths.get(path + "\\options\\git.xml");

        try {
            String content = Files.readString(git, CHARSET);
            content = content.replace(token + ":\\", currentDrive + ":\\");
            Files.writeString(git, content, CHARSET);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //JDK
        logger.info("Starting refactoring: jdk.table.xml");
        Path jdk = Paths.get(path + "\\options\\jdk.table.xml");

        try {
            String content = Files.readString(jdk, CHARSET);
            content = content.replace(token + ":/", currentDrive + ":/");
            Files.writeString(jdk, content, CHARSET);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void backupSettings(String path) {

        //GIT
        logger.info("Starting refactoring: git.xml");
        Path git = Paths.get(path + "\\options\\git.xml");

        try {
            String content = Files.readString(git, CHARSET);
            content = content.replace(currentDrive + ":\\", token + ":\\");
            Files.writeString(git, content, CHARSET);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //JDK
        logger.info("Starting refactoring: jdk.table.xml");
        Path jdk = Paths.get(path + "\\options\\jdk.table.xml");

        try {
            String content = Files.readString(jdk, CHARSET);
            content = content.replace(currentDrive + ":/", token + ":/");
            Files.writeString(jdk, content, CHARSET);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
