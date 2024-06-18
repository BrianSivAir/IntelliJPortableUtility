package it.brian.utility;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class Util {
    private static final Logger logger = LogManager.getLogger(Util.class);

    public static char getCurrentDrive() {
        return System.getProperty("user.dir").charAt(0);
    }

    public static void startIdea(String ideaExecutablePath, String projectPath) {
        logger.info("Starting IDEA");
        try {
            Process process = new ProcessBuilder(ideaExecutablePath, projectPath).start();
            logger.debug(process.info());
        } catch (IOException e) {
            logger.error("Failed to launch IDEA");
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteLocalSettings(String localSettingsFolder) {
        try {
            FileUtils.deleteDirectory(new File(localSettingsFolder));
            return true;
        } catch (IllegalArgumentException | IOException e) {
            return false;
        }

    }

    public static void invokeScript(String scriptPath) {
        logger.info("Invoking script");
        try {
            Process process = new ProcessBuilder(scriptPath).start();
            logger.debug(process.info());
        } catch (IOException e) {
            logger.error("Failed to launch Script: " + scriptPath);
            throw new RuntimeException(e);
        }
    }
}
