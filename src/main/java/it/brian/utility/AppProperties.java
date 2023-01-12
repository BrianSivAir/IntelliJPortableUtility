package it.brian.utility;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class AppProperties {

    private static final Logger logger = LogManager.getLogger(AppProperties.class);
    private static final File file = new File("./app.properties");

    private static final Properties properties = new Properties();

    private static final String OLD_DRIVE = "drive.old";
    private static final String CURRENT_DRIVE = "drve.current";
    private static final String IDE_SETTINGS_FOLDER_PC = "ide.settings.pc";
    private static final String IDE_SETTINGS_FOLDER_USB = "ide.settings.usb";
    private static final String TASK = "app.task";
    private static final String LAUNCH_IDE = "app.launchide";
    private static final String PROJECT_PATH = "project";


    public static void setOldDrive(int oldDrive) {
        properties.setProperty(OLD_DRIVE, String.valueOf(oldDrive));
    }

    public static Integer getOldDrive() {
        String oldDrive = properties.getProperty(OLD_DRIVE);
        return oldDrive == null ? null : Integer.parseInt(oldDrive);
    }

    public static void setCurrentDrive(int currentDrive) {
        properties.setProperty(CURRENT_DRIVE, String.valueOf(currentDrive));
    }

    public static Integer getCurrentDrive() {
        String currentDrive = properties.getProperty(CURRENT_DRIVE);
        return currentDrive == null ? null : Integer.parseInt(currentDrive);
    }

    public static void setIdeSettingsFolderPc(String ideSettingsFolderPc) {
        properties.setProperty(IDE_SETTINGS_FOLDER_PC, ideSettingsFolderPc);
    }

    public static String getIdeSettingsFolderPc() {
        return properties.getProperty(IDE_SETTINGS_FOLDER_PC);
    }

    public static void setIdeSettingsFolderUsb(String ideSettingsFolderUsb) {
        properties.setProperty(IDE_SETTINGS_FOLDER_USB, ideSettingsFolderUsb);
    }

    public static String getIdeSettingsFolderUsb() {
        return properties.getProperty(IDE_SETTINGS_FOLDER_USB);
    }

    public static void setTask(int task) {
        properties.setProperty(TASK, String.valueOf(task));
    }

    public static Integer getTask() {
        String task = properties.getProperty(TASK);
        return task == null ? null : Integer.parseInt(task);
    }

    public static void setLaunchIde(boolean launchIde) {
        properties.setProperty(LAUNCH_IDE, String.valueOf(launchIde));
    }

    public static Boolean getLaunchIde() {
        String launchIde = properties.getProperty(LAUNCH_IDE);
        return launchIde == null ? null : Boolean.parseBoolean(launchIde);
    }

    public static void setProjectPath(String projectPath) {
        properties.setProperty(PROJECT_PATH, projectPath);
    }

    public static String getProjectPath() {
        return properties.getProperty(PROJECT_PATH);
    }

    public static void load() {
        logger.info("Reading user settings");
        if (file.exists()) {
            try (InputStream inputStream = new FileInputStream(file)) {
                properties.load(inputStream);
            } catch (Exception e) {
                logger.error("Failed to load data from properties file");
                throw new RuntimeException(e);
            }
        }
    }

    public static void store() {
        logger.info("Writing user settings");
        createFile();
        try (OutputStream outputStream = new FileOutputStream(file)) {
            properties.store(outputStream, null);
        } catch (Exception e) {
            logger.error("Failed to store data in properties file");
            throw new RuntimeException(e);
        }
    }

    private static void createFile() {
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new RuntimeException();
                }
            } catch (Exception e) {
                logger.error("Failed to create properties file");
                throw new RuntimeException(e);
            }
        }
    }

}
