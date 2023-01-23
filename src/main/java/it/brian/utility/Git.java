package it.brian.utility;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;

public class Git {
    private static final Logger logger = LogManager.getLogger(Git.class);
    private String gitExePath;

    public Git(String gitExePath) {
        this.gitExePath = gitExePath;
    }

    public void addProjectToTrusted(String path) {
        path = path.replace("\\", "/");
        logger.info("Adding project to trusted");
        try {
            Process process = new ProcessBuilder(gitExePath, "config", "--global", "--add", "safe.directory", path).start();
            logger.debug(process.info());
        } catch (IOException e) {
            logger.error("Failed to add project to trusted");
            throw new RuntimeException(e);
        }
    }

    public void setProxySettings(String host, String username, String password) {
        logger.info("Setting proxy settings");
        String url = "http://%s:%s@%s".formatted(username, password, host);
        Process process = null;
        try {
            process = new ProcessBuilder(gitExePath, "config", "--global", "http.proxy", url).start();
            logger.debug(process.info());
        } catch (IOException e) {
            logger.error("Failed to set proxy settings");
            throw new RuntimeException(e);
        }
        logger.debug(process.info());
    }

    public void openCmd() {
        logger.info("Opening new cmd");

        ProcessBuilder b = new ProcessBuilder();
        b.environment().put("PATH", gitExePath.substring(0, gitExePath.indexOf("\\git.exe")));
        b.command("cmd", "/k", "start");
        try {
            b.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
