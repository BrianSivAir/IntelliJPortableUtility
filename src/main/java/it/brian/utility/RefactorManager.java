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
    public static void refactorSettings(String path, String oldDrive, String currentDrive) {
        Charset charset = StandardCharsets.UTF_8;

        //GIT
        logger.info("Starting refactoring: git.xml");
        Path git = Paths.get(path + "\\options\\git.xml");

        try {
            String content = Files.readString(git, charset);
            content = content.replace(oldDrive + ":\\", currentDrive + ":\\");
            Files.writeString(git, content, charset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //JDK
        logger.info("Starting refactoring: jdk.table.xml");
        Path jdk = Paths.get(path + "\\options\\jdk.table.xml");

        try {
            String content = Files.readString(jdk, charset);
            content = content.replace(oldDrive + ":/", currentDrive + ":/");
            Files.writeString(jdk, content, charset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
