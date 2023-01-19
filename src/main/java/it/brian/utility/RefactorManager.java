package it.brian.utility;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
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
    private String currentDrive;

    public RefactorManager(char currentDrive) {
        this.currentDrive = String.valueOf(currentDrive);
    }

    private enum Operation {
        LOAD,
        BACKUP
    }

    public void loadSettings(String path) {
        try {
            new File(path).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        defaultDispatcher(path, Operation.LOAD);
    }

    public void backupSettings(String path) {
        defaultDispatcher(path, Operation.BACKUP);
    }

    private void defaultDispatcher(String path, Operation operation) {
        Path jdk = Paths.get(path + "\\options\\jdk.table.xml");
        Path git = Paths.get(path + "\\options\\git.xml");
        if (operation == Operation.LOAD) {
            execute(jdk, token, currentDrive);
            execute(git, token, currentDrive);
        } else if (operation == Operation.BACKUP) {
            execute(jdk, currentDrive, token);
            execute(git, currentDrive, token);
        }
    }

    private void execute(Path path, String oldToken, String newToken) {
        logger.info("Starting refactoring: " + path.getFileName());
        try {
            String content = Files.readString(path, CHARSET);
            content = content.replace(oldToken + ':', newToken + ':');
            Files.writeString(path, content, CHARSET);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
