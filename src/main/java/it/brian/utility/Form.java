package it.brian.utility;

import com.formdev.flatlaf.FlatClientProperties;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class Form extends JFrame {
    private static final Logger logger = LogManager.getLogger(Form.class);
    private JTextField ideSettingsFolderPc;
    private JTextField ideSettingsFolderUsb;
    private JComboBox task;
    private JCheckBox launchIdeCheckBox;
    private JTextField projectPath;
    private JButton cancel;
    private JButton execute;
    private JPanel contentPane;
    private JTextField ideaExecutable;
    private JTextField gitExecutable;
    private JPanel launchPanel;
    private JButton openCmd;
    private JCheckBox addProjectToTrustedCheckBox;
    private JLabel projectPathLbl;
    private JCheckBox setProxySettingsCheckBox;
    private JTextField host;
    private JTextField username;
    private JPasswordField password;
    private JLabel hostLbl;
    private JLabel usernameLbl;
    private JLabel passwordLbl;
    private JPanel launchPadding;
    private JButton deleteLocalSettings;
    private JCheckBox setupGitCheckBox;
    private JCheckBox setupIDESettingsCheckBox;
    private JPanel settingsPadding;
    private JPanel settingsPanel;
    private JPanel gitPadding;
    private JPanel gitPanel;
    private JButton browseIdeSettingsFolderPc = new JButton();
    private JButton browseIdeSettingsFolderUsb = new JButton();
    private JButton browseProjectPath = new JButton();
    private JButton browseGitExecutable = new JButton();
    private JButton browseIdeaExecutable = new JButton();

    private void init() {
        setContentPane(contentPane);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setVisible(true);
        setTitle("IntelliJ Portable Utility");
        pack();
        setLocation(150, 100);
        setResizable(false);
        getRootPane().setDefaultButton(execute);
        load();
        refreshAllCheckBox();
        addFocusLostListeners();
        addWindowListener(windowAdapter);
    }

    private void addFocusLostListeners() {
        ideSettingsFolderPc.addFocusListener(realTimeValidator);
        ideSettingsFolderUsb.addFocusListener(realTimeValidator);
        ideSettingsFolderPc.addFocusListener(realTimeValidator);
        ideSettingsFolderUsb.addFocusListener(realTimeValidator);
        task.addFocusListener(realTimeValidator);
        gitExecutable.addFocusListener(realTimeValidator);
        projectPath.addFocusListener(realTimeValidator);
        host.addFocusListener(realTimeValidator);
        username.addFocusListener(realTimeValidator);
        password.addFocusListener(realTimeValidator);
        ideaExecutable.addFocusListener(realTimeValidator);
    }

    public Form() {
        init();

        configureBrowseButtons();

        autoReplaceDriveInPaths();

        setupIDESettingsCheckBox.addActionListener(e -> refreshAllCheckBox());
        setupGitCheckBox.addActionListener(e -> refreshAllCheckBox());
        addProjectToTrustedCheckBox.addActionListener(e -> refreshAllCheckBox());
        setProxySettingsCheckBox.addActionListener(e -> refreshAllCheckBox());
        launchIdeCheckBox.addActionListener(e -> refreshAllCheckBox());

        execute.addActionListener(e -> {
            //VALIDATION
            if (validateThis()) {
                //PERSISTENCE
                persist();
                //EXECUTION
                ////copy
                if (setupIDESettingsCheckBox.isSelected()) {
                    CopyManager copyManager = new CopyManager(ideSettingsFolderPc.getText(), ideSettingsFolderUsb.getText());
                    RefactorManager refactorManager = new RefactorManager(Util.getCurrentDrive());
                    switch (task.getSelectedIndex()) {
                        case 1 -> {
                            copyManager.copyUsbToPc();
                            refactorManager.loadSettings(ideSettingsFolderPc.getText());
                        }
                        case 2 -> {
                            copyManager.copyPcToUsb();
                            refactorManager.backupSettings(ideSettingsFolderUsb.getText());
                        }
                    }
                }
                ////git
                if (setupGitCheckBox.isSelected()) {
                    Git git = new Git(gitExecutable.getText());
                    if (addProjectToTrustedCheckBox.isSelected()) {
                        git.addProjectToTrusted(projectPath.getText());
                    }

                    if (setProxySettingsCheckBox.isSelected()) {
                        git.setProxySettings(host.getText(), username.getText(), password.getText());
                    }
                }

                ////launch
                if (launchIdeCheckBox.isSelected()) {
                    String arg = "";
                    if (setupGitCheckBox.isSelected() && addProjectToTrustedCheckBox.isSelected()) {
                        arg = projectPath.getText();
                    }
                    Util.startIdea(ideaExecutable.getText(), arg);
                }
            }

        });
        cancel.addActionListener(e -> {
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });

        openCmd.addActionListener(e -> {
            if (Validator.isValidGitExecutable(gitExecutable.getText())) {
                Git git = new Git(gitExecutable.getText());
                git.openCmd();
            } else {
                JOptionPane.showMessageDialog(
                        SwingUtilities.windowForComponent(Form.this),
                        "Invalid git executable",
                        "Error",
                        JOptionPane.ERROR_MESSAGE,
                        UIManager.getIcon("OptionPane.errorIcon")
                );
            }
        });


        deleteLocalSettings.addActionListener(e -> {
            if (Util.deleteLocalSettings(ideSettingsFolderPc.getText())) {
                JOptionPane.showMessageDialog(
                        SwingUtilities.windowForComponent(Form.this),
                        "Local IDE settings deleted successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        SwingUtilities.windowForComponent(Form.this),
                        "Failed to delete local IDE settings",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    private void configureBrowseButtons() {
        browseIdeSettingsFolderPc.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (jFileChooser.showDialog(this, null) == JFileChooser.APPROVE_OPTION) {
                ideSettingsFolderPc.setText(jFileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        browseIdeSettingsFolderPc.setIcon(UIManager.getIcon("Tree.closedIcon"));
        ideSettingsFolderPc.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, browseIdeSettingsFolderPc);

        browseIdeSettingsFolderUsb.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (jFileChooser.showDialog(this, null) == JFileChooser.APPROVE_OPTION) {
                ideSettingsFolderUsb.setText(jFileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        browseIdeSettingsFolderUsb.setIcon(UIManager.getIcon("Tree.closedIcon"));
        ideSettingsFolderUsb.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, browseIdeSettingsFolderUsb);

        browseIdeaExecutable.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jFileChooser.setFileFilter(new ExeFilter());
            if (jFileChooser.showDialog(this, null) == JFileChooser.APPROVE_OPTION) {
                ideaExecutable.setText(jFileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        browseIdeaExecutable.setIcon(UIManager.getIcon("Tree.closedIcon"));
        ideaExecutable.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, browseIdeaExecutable);

        browseProjectPath.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (jFileChooser.showDialog(this, null) == JFileChooser.APPROVE_OPTION) {
                projectPath.setText(jFileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        browseProjectPath.setIcon(UIManager.getIcon("Tree.closedIcon"));
        projectPath.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, browseProjectPath);

        browseGitExecutable.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jFileChooser.setFileFilter(new ExeFilter());
            if (jFileChooser.showDialog(this, null) == JFileChooser.APPROVE_OPTION) {
                gitExecutable.setText(jFileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        browseGitExecutable.setIcon(UIManager.getIcon("Tree.closedIcon"));
        gitExecutable.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, browseGitExecutable);
    }

    private void autoReplaceDriveInPaths() {
        List<Character> drivesList = new ArrayList<>();
        List<JTextField> ts = new ArrayList<>();

        if (setupIDESettingsCheckBox.isSelected()) {
            if (ideSettingsFolderUsb.getText().length() > 0) {
                drivesList.add(ideSettingsFolderUsb.getText().charAt(0));
                ts.add(ideSettingsFolderUsb);
            }
        }

        if (setupGitCheckBox.isSelected()) {
            if (gitExecutable.getText().length() > 0) {
                drivesList.add(gitExecutable.getText().charAt(0));
                ts.add(gitExecutable);
            }
            if (projectPath.getText().length() > 0 && addProjectToTrustedCheckBox.isSelected()) {
                drivesList.add(projectPath.getText().charAt(0));
                ts.add(projectPath);
            }
        }

        if (launchIdeCheckBox.isSelected()) {
            if (ideaExecutable.getText().length() > 0) {
                drivesList.add(ideaExecutable.getText().charAt(0));
                ts.add(ideaExecutable);
            }
        }

        if (drivesList.stream().distinct().count() == 1) {
            char oDrive = drivesList.get(0);
            char cDrive = Util.getCurrentDrive();

            if (oDrive != cDrive) {
                int resp = JOptionPane.showConfirmDialog(
                        SwingUtilities.windowForComponent(Form.this),
                        "Detected old drive name in paths.\nDo you want to replace them with new drive name?",
                        "Fast refactor available",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (resp == JOptionPane.YES_OPTION) {
                    for (JTextField textField : ts) {
                        textField.setText(cDrive + textField.getText().substring(1));
                    }
                }
            }
        }
    }

    private void refreshAllCheckBox() {
        if (addProjectToTrustedCheckBox.isSelected()) {
            projectPathLbl.setVisible(true);
            projectPath.setVisible(true);
        } else {
            projectPathLbl.setVisible(false);
            projectPath.setVisible(false);
        }

        if (setProxySettingsCheckBox.isSelected()) {
            hostLbl.setVisible(true);
            usernameLbl.setVisible(true);
            passwordLbl.setVisible(true);
            host.setVisible(true);
            username.setVisible(true);
            password.setVisible(true);
        } else {
            hostLbl.setVisible(false);
            usernameLbl.setVisible(false);
            passwordLbl.setVisible(false);
            host.setVisible(false);
            username.setVisible(false);
            password.setVisible(false);
        }

        if (setupIDESettingsCheckBox.isSelected()) {
            settingsPadding.setVisible(true);
        } else {
            settingsPadding.setVisible(false);
        }

        if (setupGitCheckBox.isSelected()) {
            gitPadding.setVisible(true);
        } else {
            gitPadding.setVisible(false);
        }

        if (launchIdeCheckBox.isSelected()) {
            launchPadding.setVisible(true);
        } else {
            launchPadding.setVisible(false);
        }
    }

    private LinkedMap<JComponent, String> getInvalidComponents() {
        LinkedMap<JComponent, String> invalidComponents = new LinkedMap<>();

        if (setupIDESettingsCheckBox.isSelected()) {
            if (!Validator.isValidIdeSettingsFolderPc(ideSettingsFolderPc.getText())) {
                invalidComponents.put(ideSettingsFolderPc, "Invalid IDE settings folder PC");
            }
            if (!Validator.isValidIdeSettingsFolderUsb(ideSettingsFolderUsb.getText())) {
                invalidComponents.put(ideSettingsFolderUsb, "Invalid IDE settings folder USB");
            }
            if (!Validator.isValidTask(task.getSelectedIndex())) {
                invalidComponents.put(task, "Invalid task");
            }
        }

        if (setupGitCheckBox.isSelected()) {
            if (!Validator.isValidGitExecutable(gitExecutable.getText())) {
                invalidComponents.put(gitExecutable, "Invalid git executable");
            }
            if (addProjectToTrustedCheckBox.isSelected()) {
                if (!Validator.isValidProjectPath(projectPath.getText())) {
                    invalidComponents.put(projectPath, "Invalid project path");
                }
            }
            if (setProxySettingsCheckBox.isSelected()) {
                if (!Validator.isValidHost(host.getText())) {
                    invalidComponents.put(host, "Invalid git proxy host");
                } else if (!Validator.isValidUsername(username.getText())) {
                    invalidComponents.put(username, "Invalid git proxy username");
                } else if (!Validator.isValidPassword(password.getText())) {
                    invalidComponents.put(password, "Invalid git proxy password");
                }
            }
        }

        if (launchIdeCheckBox.isSelected()) {
            if (!Validator.isValidIdeaExecutable(ideaExecutable.getText())) {
                invalidComponents.put(ideaExecutable, "Invalid IDEA executable");
            }
        }
        return invalidComponents;
    }

    private boolean validateThis() {
        LinkedMap<JComponent, String> invalidComponents = getInvalidComponents();

        JComponent firstKey = null;

        try {
            firstKey = invalidComponents.firstKey();
        } catch (NoSuchElementException e) {
            return true;
        }

        String message = invalidComponents.get(firstKey);

        JOptionPane.showMessageDialog(
                SwingUtilities.windowForComponent(Form.this),
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE,
                UIManager.getIcon("OptionPane.errorIcon")
        );
        return false;
    }

    private void load() {
        AppProperties.load();
        setupIDESettingsCheckBox.setSelected(Optional.ofNullable(AppProperties.getSetupIdeSettings()).orElse(true));
        ideSettingsFolderPc.setText(Optional.ofNullable(AppProperties.getIdeSettingsFolderPc()).orElse(System.getenv("APPDATA") + "\\JetBrains"));
        ideSettingsFolderUsb.setText(Optional.ofNullable(AppProperties.getIdeSettingsFolderUsb()).orElse(""));
        task.setSelectedIndex(Optional.ofNullable(AppProperties.getTask()).orElse(0));
        setupGitCheckBox.setSelected(Optional.ofNullable(AppProperties.getSetupGit()).orElse(true));
        gitExecutable.setText(Optional.ofNullable(AppProperties.getGitExecutable()).orElse(""));
        addProjectToTrustedCheckBox.setSelected(Optional.ofNullable(AppProperties.getAddProjectToTrusted()).orElse(false));
        projectPath.setText(Optional.ofNullable(AppProperties.getProjectPath()).orElse(""));
        setProxySettingsCheckBox.setSelected(Optional.ofNullable(AppProperties.getSetProxySettings()).orElse(false));
        host.setText(Optional.ofNullable(AppProperties.getHost()).orElse(""));
        username.setText(Optional.ofNullable(AppProperties.getUsername()).orElse(""));
        password.setText(Optional.ofNullable(AppProperties.getPassword()).orElse(""));
        launchIdeCheckBox.setSelected(Optional.ofNullable(AppProperties.getLaunchIde()).orElse(true));
        ideaExecutable.setText(Optional.ofNullable(AppProperties.getIdeaExecutable()).orElse(""));
    }

    private void persist() {
        AppProperties.setSetupIdeSettings(setupIDESettingsCheckBox.isSelected());
        AppProperties.setIdeSettingsFolderPc(ideSettingsFolderPc.getText());
        AppProperties.setIdeSettingsFolderUsb(ideSettingsFolderUsb.getText());
        AppProperties.setTask(task.getSelectedIndex());
        AppProperties.setSetupGit(setupGitCheckBox.isSelected());
        AppProperties.setGitExecutable(gitExecutable.getText());
        AppProperties.setAddProjectToTrusted(addProjectToTrustedCheckBox.isSelected());
        AppProperties.setProjectPath(projectPath.getText());
        AppProperties.setSetProxySettings(setProxySettingsCheckBox.isSelected());
        AppProperties.setHost(host.getText());
        AppProperties.setUsername(username.getText());
        AppProperties.setPassword(password.getText());
        AppProperties.setLaunchIde(launchIdeCheckBox.isSelected());
        AppProperties.setIdeaExecutable(ideaExecutable.getText());
        AppProperties.store();
    }

    FocusAdapter realTimeValidator = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
            LinkedMap<JComponent, String> invalidComponents = getInvalidComponents();
            JComponent jcomponent = ((JComponent) e.getComponent());
            if (invalidComponents.containsKey(jcomponent)) {
                jcomponent.setToolTipText(invalidComponents.get(jcomponent));
                jcomponent.putClientProperty("JComponent.outline", "error");
            } else {
                jcomponent.setToolTipText(null);
                jcomponent.putClientProperty("JComponent.outline", null);
            }
        }
    };

    WindowAdapter windowAdapter = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            logger.info("Closing");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        }
    };


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(7, 4, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final Spacer spacer1 = new Spacer();
        contentPane.add(spacer1, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        execute = new JButton();
        execute.setText("Execute");
        contentPane.add(execute, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancel = new JButton();
        cancel.setText("Cancel");
        contentPane.add(cancel, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        launchIdeCheckBox = new JCheckBox();
        launchIdeCheckBox.setText("Launch IDE");
        contentPane.add(launchIdeCheckBox, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gitPadding = new JPanel();
        gitPadding.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(gitPadding, new GridConstraints(3, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gitPadding.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Git", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        gitPanel = new JPanel();
        gitPanel.setLayout(new GridLayoutManager(9, 2, new Insets(0, 0, 0, 0), -1, -1));
        gitPadding.add(gitPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gitPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label1 = new JLabel();
        label1.setText("Git executable:");
        gitPanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        gitPanel.add(spacer2, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        gitExecutable = new JTextField();
        gitPanel.add(gitExecutable, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        projectPathLbl = new JLabel();
        projectPathLbl.setText("Project path:");
        gitPanel.add(projectPathLbl, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        projectPath = new JTextField();
        projectPath.setText("");
        gitPanel.add(projectPath, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        openCmd = new JButton();
        openCmd.setText("Open CMD");
        gitPanel.add(openCmd, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addProjectToTrustedCheckBox = new JCheckBox();
        addProjectToTrustedCheckBox.setText("Add project to trusted");
        gitPanel.add(addProjectToTrustedCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setProxySettingsCheckBox = new JCheckBox();
        setProxySettingsCheckBox.setText("Set proxy settings");
        gitPanel.add(setProxySettingsCheckBox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        hostLbl = new JLabel();
        hostLbl.setText("Host:");
        gitPanel.add(hostLbl, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        usernameLbl = new JLabel();
        usernameLbl.setText("Username:");
        gitPanel.add(usernameLbl, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        passwordLbl = new JLabel();
        passwordLbl.setText("Password:");
        gitPanel.add(passwordLbl, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        host = new JTextField();
        gitPanel.add(host, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        username = new JTextField();
        gitPanel.add(username, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        password = new JPasswordField();
        gitPanel.add(password, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        settingsPadding = new JPanel();
        settingsPadding.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(settingsPadding, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        settingsPadding.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Settings", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        settingsPadding.add(settingsPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        settingsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final Spacer spacer3 = new Spacer();
        settingsPanel.add(spacer3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("IDE settings folder PC:");
        settingsPanel.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ideSettingsFolderPc = new JTextField();
        settingsPanel.add(ideSettingsFolderPc, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("IDE settings folder USB:");
        settingsPanel.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ideSettingsFolderUsb = new JTextField();
        settingsPanel.add(ideSettingsFolderUsb, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Task:");
        settingsPanel.add(label4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        task = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Select a task");
        defaultComboBoxModel1.addElement("Refactor and copy from USB to PC");
        defaultComboBoxModel1.addElement("Refactor and copy from PC to USB");
        task.setModel(defaultComboBoxModel1);
        settingsPanel.add(task, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        launchPadding = new JPanel();
        launchPadding.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(launchPadding, new GridConstraints(5, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        launchPadding.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Launch", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        launchPanel = new JPanel();
        launchPanel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        launchPadding.add(launchPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        launchPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label5 = new JLabel();
        label5.setText("IDEA executable:");
        launchPanel.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        launchPanel.add(spacer4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        ideaExecutable = new JTextField();
        launchPanel.add(ideaExecutable, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        deleteLocalSettings = new JButton();
        deleteLocalSettings.setText("Delete local settings");
        contentPane.add(deleteLocalSettings, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setupGitCheckBox = new JCheckBox();
        setupGitCheckBox.setSelected(false);
        setupGitCheckBox.setText("Setup git");
        contentPane.add(setupGitCheckBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setupIDESettingsCheckBox = new JCheckBox();
        setupIDESettingsCheckBox.setText("Setup IDE settings");
        contentPane.add(setupIDESettingsCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
