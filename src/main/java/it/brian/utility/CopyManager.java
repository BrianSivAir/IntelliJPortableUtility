package it.brian.utility;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class CopyManager {
    private static final Logger logger = LogManager.getLogger(CopyManager.class);
    private File ideSettingsFolderPcFile;
    private File ideSettingsFolderUsbFile;

    public CopyManager(String ideSettingsFolderPc, String ideSettingsFolderUsb) throws NullPointerException {
        ideSettingsFolderPcFile = new File(ideSettingsFolderPc);
        ideSettingsFolderUsbFile = new File(ideSettingsFolderUsb);
    }

    public void copyPcToUsb() {
        logger.info("Copying settings PC -> USB");
        try {
            FileUtils.copyDirectory(ideSettingsFolderPcFile, ideSettingsFolderUsbFile);
        } catch (IOException e) {
            logger.error("Failed to copy directory");
            throw new RuntimeException(e);
        }
    }

    public void copyUsbToPc() {
        logger.info("Copying settings USB -> PC");
        try {
            FileUtils.copyDirectory(ideSettingsFolderUsbFile, ideSettingsFolderPcFile);
        } catch (IOException e) {
            logger.error("Failed to copy directory");
            throw new RuntimeException(e);
        }
    }
}
