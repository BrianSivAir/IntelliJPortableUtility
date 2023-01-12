package it.brian.utility;

public class Validator {
    public static boolean isValidOldDrive(int oldDrive) {
        return 0 < oldDrive && oldDrive < 24;
    }
    public static boolean isValidCurrentDrive(int currentDrive) {
        return 0 < currentDrive && currentDrive < 24;
    }
    public static boolean isValidIdeSettingsFolderPc(String ideSettingsFolderPc) {
        return ideSettingsFolderPc.trim().length() > 0;
    }
    public static boolean isValidIdeSettingsFolderUsb(String ideSettingsFolderUsb) {
        return ideSettingsFolderUsb.trim().length() > 0;
    }
    public static boolean isValidTask(int task) {
        return 0 < task && task < 4;
    }
    public static boolean isValidIdeaExecutable(String ideaExecutable) {
        return ideaExecutable.trim().length() > 0;
    }
    public static boolean isValidProjectPath(String projectPath) {
        return projectPath.trim().length() > 0;
    }
}
