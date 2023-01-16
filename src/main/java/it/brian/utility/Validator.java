package it.brian.utility;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class Validator {
//    public static boolean isValidOldDrive(int oldDrive) {
//        return 0 < oldDrive && oldDrive < 24;
//    }
//    public static boolean isValidCurrentDrive(int currentDrive) {
//        return 0 < currentDrive && currentDrive < 24;
//    }
    public static boolean isValidIdeSettingsFolderPc(String ideSettingsFolderPc) {
        return new File(ideSettingsFolderPc).isDirectory();
    }
    public static boolean isValidIdeSettingsFolderUsb(String ideSettingsFolderUsb) {
        return new File(ideSettingsFolderUsb).isDirectory();
    }

    public static boolean isValidTask(int task) {
        return 0 < task && task < 4;
    }

    public static boolean isValidGitExecutable(String gitExecutable) {
        File file = new File(gitExecutable);
        return file.isFile() && "exe".equals(FilenameUtils.getExtension(file.getName()));
    }

    public static boolean isValidProjectPath(String projectPath) {
        return new File(projectPath).isDirectory();
    }

    public static boolean isValidHost(String host) {
        return host.trim().length() > 0;
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
