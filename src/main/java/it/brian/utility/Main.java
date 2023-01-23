package it.brian.utility;

import com.ezware.dialog.task.TaskDialogs;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.IOException;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) throws IOException {
        FlatMacDarkLaf.setup();
        UIManager.put("PasswordField.showRevealButton", true);
        Form form = new Form();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> TaskDialogs.showException(e));
    }


}