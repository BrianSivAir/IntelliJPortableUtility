package it.brian.utility;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

public class AppProperties {

    private static final Logger logger = LogManager.getLogger(AppProperties.class);
    private static final File file = new File("./app.properties");

    private static final Properties properties = new Properties();

    private static final String IDE_SETTINGS_FOLDER_PC = "ide.settings.pc";
    private static final String IDE_SETTINGS_FOLDER_USB = "ide.settings.usb";
    private static final String TASK = "app.task";
    private static final String GIT_EXECUTABLE = "git.exe";
    private static final String ADD_PROJECT_TO_TRUSTED = "git.project.addtotrusted";
    private static final String PROJECT_PATH = "git.project.path";
    private static final String SET_PROXY_SETTINGS = "git.proxy.setproxysettings";
    private static final String HOST = "git.proxy.host";
    private static final String USERNAME = "git.proxy.username";
    private static final String PASSWORD = "git.proxy.password";
    private static final String LAUNCH_IDE = "app.launchide";
    private static final String IDEA_EXECUTABLE = "ide.executable";


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

    public static void setGitExecutable(String gitExecutable) {
        properties.setProperty(GIT_EXECUTABLE, gitExecutable);
    }

    public static String getGitExecutable() {
        return properties.getProperty(GIT_EXECUTABLE);
    }

    public static void setAddProjectToTrusted(Boolean addProjectToTrusted) {
        properties.setProperty(ADD_PROJECT_TO_TRUSTED, String.valueOf(addProjectToTrusted));
    }

    public static Boolean getAddProjectToTrusted() {
        String addProjectToTrusted = properties.getProperty(ADD_PROJECT_TO_TRUSTED);
        return addProjectToTrusted == null ? null : Boolean.parseBoolean(addProjectToTrusted);
    }

    public static void setProjectPath(String projectPath) {
        properties.setProperty(PROJECT_PATH, projectPath);
    }

    public static String getProjectPath() {
        return properties.getProperty(PROJECT_PATH);
    }

    public static void setSetProxySettings(Boolean setProxySettings) {
        properties.setProperty(SET_PROXY_SETTINGS, String.valueOf(setProxySettings));
    }

    public static Boolean getSetProxySettings() {
        String setProxySettings = properties.getProperty(SET_PROXY_SETTINGS);
        return setProxySettings == null ? null : Boolean.parseBoolean(setProxySettings);
    }

    public static void setHost(String host) {
        properties.setProperty(HOST, host);
    }

    public static String getHost() {
        return properties.getProperty(HOST);
    }

    public static void setUsername(String username) {
        properties.setProperty(USERNAME, username);
    }

    public static String getUsername() {
        return properties.getProperty(USERNAME);
    }

    public static void setPassword(String password) {
        properties.setProperty(PASSWORD, Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8)));
    }

    public static String getPassword() {
        String password = properties.getProperty(PASSWORD);
        return password == null ? null : new String(Base64.getDecoder().decode(password));
    }

    public static void setLaunchIde(boolean launchIde) {
        properties.setProperty(LAUNCH_IDE, String.valueOf(launchIde));
    }

    public static Boolean getLaunchIde() {
        String launchIde = properties.getProperty(LAUNCH_IDE);
        return launchIde == null ? null : Boolean.parseBoolean(launchIde);
    }

    public static void setIdeaExecutable(String ideaExecutable) {
        properties.setProperty(IDEA_EXECUTABLE, ideaExecutable);
    }

    public static String getIdeaExecutable() {
        return properties.getProperty(IDEA_EXECUTABLE);
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
        logger.info("No user settings found");
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
