package it.brian.utility;

import com.formdev.flatlaf.FlatClientProperties;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.commons.io.Charsets;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class Form extends JFrame {
    private JComboBox oldDrive;
    private JComboBox currentDrive;
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
    private JButton browseIdeSettingsFolderPc = new JButton();
    private JButton browseIdeSettingsFolderUsb = new JButton();
    private JButton browseProjectPath = new JButton();
    private JButton browseGitExecutable = new JButton();
    private JButton browseIdeaExecutable = new JButton();

    private void init() {
        setContentPane(contentPane);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setTitle("IntelliJ Portable Utility");
        pack();
        setLocation(150, 100);
        setResizable(false);
        getRootPane().setDefaultButton(execute);
        load();
        refreshAllCheckBox();
    }

    public Form() {
        init();

        configureBrowseButtons();

        execute.addActionListener(e -> {
            //VALIDATION
            if (validateThis()) {
                //PERSISTENCE
                persist();
                //EXECUTION
                ////copy
                CopyManager copyManager = new CopyManager(ideSettingsFolderPc.getText(), ideSettingsFolderUsb.getText());
                RefactorManager refactorManager = new RefactorManager(Util.getCurrentDrive());
                switch (task.getSelectedIndex()) {
                    case 1 -> {
                        refactorManager.loadSettings(ideSettingsFolderUsb.getText());
                        copyManager.copyUsbToPc();
                    }
                    case 2 -> {
                        copyManager.copyPcToUsb();
                        refactorManager.backupSettings(ideSettingsFolderUsb.getText());
                    }
                }
                ////git
                Git git = new Git(gitExecutable.getText());
                if (addProjectToTrustedCheckBox.isSelected()) {
                    git.addProjectToTrusted(projectPath.getText());
                }

                if (setProxySettingsCheckBox.isSelected()) {
                    git.setProxySettings(host.getText(), username.getText(), password.getText());
                }

                ////launch
                if (launchIdeCheckBox.isSelected()) {
                    Util.startIdea(ideaExecutable.getText(), projectPath.getText());
                }
            }

        });
        cancel.addActionListener(e -> {
            dispose();
            System.exit(0);
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

        addProjectToTrustedCheckBox.addActionListener(e -> refreshAllCheckBox());
        setProxySettingsCheckBox.addActionListener(e -> refreshAllCheckBox());
        launchIdeCheckBox.addActionListener(e -> refreshAllCheckBox());


        autoReplaceDriveInPaths();
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

        if (ideaExecutable.getText().length() > 0) {
            drivesList.add(ideSettingsFolderUsb.getText().charAt(0));
            ts.add(ideSettingsFolderUsb);
        }
        if (gitExecutable.getText().length() > 0) {
            drivesList.add(gitExecutable.getText().charAt(0));
            ts.add(gitExecutable);
        }
        if (projectPath.getText().length() > 0 && addProjectToTrustedCheckBox.isSelected()) {
            drivesList.add(projectPath.getText().charAt(0));
            ts.add(projectPath);
        }
        if (ideaExecutable.getText().length() > 0 && launchIdeCheckBox.isSelected()) {
            drivesList.add(ideaExecutable.getText().charAt(0));
            ts.add(ideaExecutable);
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

        if (launchIdeCheckBox.isSelected()) {
            launchPadding.setVisible(true);
        } else {
            launchPadding.setVisible(false);
        }
    }

    private boolean validateThis() {
        String message = "";

       /* if (!Validator.isValidOldDrive(oldDrive.getSelectedIndex())) {
            message = "Invalid old drive";
        } else if (!Validator.isValidCurrentDrive(currentDrive.getSelectedIndex())) {
            message = "Invalid current drive";
        } else */
        if (!Validator.isValidIdeSettingsFolderPc(ideSettingsFolderPc.getText())) {
            message = "Invalid IDE settings folder PC";
        } else if (!Validator.isValidIdeSettingsFolderUsb(ideSettingsFolderUsb.getText())) {
            message = "Invalid IDE settings folder USB";
        } else if (!Validator.isValidTask(task.getSelectedIndex())) {
            message = "Invalid task";
        } else if (!Validator.isValidGitExecutable(gitExecutable.getText())) {
            message = "Invalid git executable";
        } else if (!Validator.isValidProjectPath(projectPath.getText())) {
            message = "Invalid project path";
        } else if (!Validator.isValidHost(host.getText())) {
            message = "Invalid git proxy host";
        } else if (!Validator.isValidUsername(username.getText())) {
            message = "Invalid git proxy username";
        } else if (!Validator.isValidPassword(password.getText())) {
            message = "Invalid git proxy password";
        } else if (!Validator.isValidIdeaExecutable(ideaExecutable.getText())) {
            message = "Invalid IDEA executable";
        }

        if (!message.equals("")) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.windowForComponent(Form.this),
                    message,
                    "Error",
                    JOptionPane.ERROR_MESSAGE,
                    UIManager.getIcon("OptionPane.errorIcon")
            );
            return false;
        }
        return true;
    }

    private void load() {
        AppProperties.load();
        /*oldDrive.setSelectedIndex(Optional.ofNullable(AppProperties.getOldDrive()).orElse(0));
        currentDrive.setSelectedIndex(Optional.ofNullable(AppProperties.getCurrentDrive()).orElse(0));*/
        ideSettingsFolderPc.setText(Optional.ofNullable(AppProperties.getIdeSettingsFolderPc()).orElse(System.getenv("APPDATA") + "\\JetBrains"));
        ideSettingsFolderUsb.setText(Optional.ofNullable(AppProperties.getIdeSettingsFolderUsb()).orElse(""));
        task.setSelectedIndex(Optional.ofNullable(AppProperties.getTask()).orElse(0));
        gitExecutable.setText(Optional.ofNullable(AppProperties.getGitExecutable()).orElse(""));
        addProjectToTrustedCheckBox.setSelected(Optional.ofNullable(AppProperties.getAddProjectToTrusted()).orElse(false));
        projectPath.setText(Optional.ofNullable(AppProperties.getProjectPath()).orElse(""));
        setProxySettingsCheckBox.setSelected(Optional.ofNullable(AppProperties.getSetProxySettings()).orElse(false));
        host.setText(Optional.ofNullable(AppProperties.getHost()).orElse(""));
        username.setText(Optional.ofNullable(AppProperties.getUsername()).orElse(""));
        password.setText(Optional.ofNullable(AppProperties.getPassword()).orElse(""));
        launchIdeCheckBox.setSelected(Optional.ofNullable(AppProperties.getLaunchIde()).orElse(false));
        ideaExecutable.setText(Optional.ofNullable(AppProperties.getIdeaExecutable()).orElse(""));
    }

    private void persist() {
/*        AppProperties.setOldDrive(oldDrive.getSelectedIndex());
        AppProperties.setCurrentDrive(currentDrive.getSelectedIndex());*/
        AppProperties.setIdeSettingsFolderPc(ideSettingsFolderPc.getText());
        AppProperties.setIdeSettingsFolderUsb(ideSettingsFolderUsb.getText());
        AppProperties.setTask(task.getSelectedIndex());
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
        contentPane.setLayout(new GridLayoutManager(5, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final Spacer spacer1 = new Spacer();
        contentPane.add(spacer1, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        execute = new JButton();
        execute.setText("Execute");
        contentPane.add(execute, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancel = new JButton();
        cancel.setText("Cancel");
        contentPane.add(cancel, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        launchIdeCheckBox = new JCheckBox();
        launchIdeCheckBox.setText("Launch IDE");
        contentPane.add(launchIdeCheckBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Git", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(9, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label1 = new JLabel();
        label1.setText("Git executable:");
        panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel2.add(spacer2, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        gitExecutable = new JTextField();
        panel2.add(gitExecutable, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        projectPathLbl = new JLabel();
        projectPathLbl.setText("Project path:");
        panel2.add(projectPathLbl, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        projectPath = new JTextField();
        projectPath.setText("");
        panel2.add(projectPath, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        openCmd = new JButton();
        openCmd.setText("Open CMD");
        panel2.add(openCmd, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addProjectToTrustedCheckBox = new JCheckBox();
        addProjectToTrustedCheckBox.setText("Add project to trusted");
        panel2.add(addProjectToTrustedCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setProxySettingsCheckBox = new JCheckBox();
        setProxySettingsCheckBox.setText("Set proxy settings");
        panel2.add(setProxySettingsCheckBox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        hostLbl = new JLabel();
        hostLbl.setText("Host:");
        panel2.add(hostLbl, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        usernameLbl = new JLabel();
        usernameLbl.setText("Username:");
        panel2.add(usernameLbl, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        passwordLbl = new JLabel();
        passwordLbl.setText("Password:");
        panel2.add(passwordLbl, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        host = new JTextField();
        panel2.add(host, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        username = new JTextField();
        panel2.add(username, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        password = new JPasswordField();
        panel2.add(password, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Settings", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(5, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label2 = new JLabel();
        label2.setText("Drive:");
        panel4.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel4.add(spacer3, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        oldDrive = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Select old drive");
        defaultComboBoxModel1.addElement("D");
        defaultComboBoxModel1.addElement("E");
        defaultComboBoxModel1.addElement("F");
        defaultComboBoxModel1.addElement("G");
        defaultComboBoxModel1.addElement("H");
        defaultComboBoxModel1.addElement("I");
        defaultComboBoxModel1.addElement("J");
        defaultComboBoxModel1.addElement("K");
        defaultComboBoxModel1.addElement("L");
        defaultComboBoxModel1.addElement("M");
        defaultComboBoxModel1.addElement("N");
        defaultComboBoxModel1.addElement("O");
        defaultComboBoxModel1.addElement("P");
        defaultComboBoxModel1.addElement("Q");
        defaultComboBoxModel1.addElement("R");
        defaultComboBoxModel1.addElement("S");
        defaultComboBoxModel1.addElement("T");
        defaultComboBoxModel1.addElement("U");
        defaultComboBoxModel1.addElement("V");
        defaultComboBoxModel1.addElement("W");
        defaultComboBoxModel1.addElement("X");
        defaultComboBoxModel1.addElement("Y");
        defaultComboBoxModel1.addElement("Z");
        oldDrive.setModel(defaultComboBoxModel1);
        panel4.add(oldDrive, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        currentDrive = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("Select current drive");
        defaultComboBoxModel2.addElement("D");
        defaultComboBoxModel2.addElement("E");
        defaultComboBoxModel2.addElement("F");
        defaultComboBoxModel2.addElement("G");
        defaultComboBoxModel2.addElement("H");
        defaultComboBoxModel2.addElement("I");
        defaultComboBoxModel2.addElement("J");
        defaultComboBoxModel2.addElement("K");
        defaultComboBoxModel2.addElement("L");
        defaultComboBoxModel2.addElement("M");
        defaultComboBoxModel2.addElement("N");
        defaultComboBoxModel2.addElement("O");
        defaultComboBoxModel2.addElement("P");
        defaultComboBoxModel2.addElement("Q");
        defaultComboBoxModel2.addElement("R");
        defaultComboBoxModel2.addElement("S");
        defaultComboBoxModel2.addElement("T");
        defaultComboBoxModel2.addElement("U");
        defaultComboBoxModel2.addElement("V");
        defaultComboBoxModel2.addElement("W");
        defaultComboBoxModel2.addElement("X");
        defaultComboBoxModel2.addElement("Y");
        defaultComboBoxModel2.addElement("Z");
        currentDrive.setModel(defaultComboBoxModel2);
        panel4.add(currentDrive, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("IDE settings folder PC:");
        panel4.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ideSettingsFolderPc = new JTextField();
        panel4.add(ideSettingsFolderPc, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("IDE settings folder USB:");
        panel4.add(label4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ideSettingsFolderUsb = new JTextField();
        panel4.add(ideSettingsFolderUsb, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Task:");
        panel4.add(label5, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        task = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("Select a task");
        defaultComboBoxModel3.addElement("Refactor and copy from USB to PC");
        defaultComboBoxModel3.addElement("Refactor and copy from PC to USB");
        task.setModel(defaultComboBoxModel3);
        panel4.add(task, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        launchPadding = new JPanel();
        launchPadding.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(launchPadding, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        launchPadding.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Launch", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        launchPanel = new JPanel();
        launchPanel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        launchPadding.add(launchPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        launchPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label6 = new JLabel();
        label6.setText("IDEA executable:");
        launchPanel.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        launchPanel.add(spacer4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        ideaExecutable = new JTextField();
        launchPanel.add(ideaExecutable, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
