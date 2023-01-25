package it.brian.utility;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class Validator {
    public static boolean isValidIdeSettingsFolderPc(String ideSettingsFolderPc) {
        if ("".equals(ideSettingsFolderPc)) {
            return false;
        }
        try {
            Paths.get(ideSettingsFolderPc);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    public static boolean isValidIdeSettingsFolderUsb(String ideSettingsFolderUsb) {
        if ("".equals(ideSettingsFolderUsb)) {
            return false;
        }
        try {
            Paths.get(ideSettingsFolderUsb);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isValidTask(int task) {
        return 0 < task && task < 3;
    }

    public static boolean isValidGitExecutable(String gitExecutable) {
        File file = new File(gitExecutable);
        return file.isFile() && "exe".equals(FilenameUtils.getExtension(file.getName()));
    }

    public static boolean isValidProjectPath(String projectPath) {
        return new File(projectPath).isDirectory();
    }

    public static boolean isValidHost(String host) {
        return Pattern.matches("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?):[0-9]{1,5}$", host);
    }

    public static boolean isValidUsername(String username) {
        return username.trim().length() > 0;
    }

    public static boolean isValidPassword(String password) {
        return password.trim().length() > 0;
    }
    public static boolean isValidIdeaExecutable(String ideaExecutable) {
        File file = new File(ideaExecutable);
        return file.isFile() && "exe".equals(FilenameUtils.getExtension(file.getName()));
    }
}
