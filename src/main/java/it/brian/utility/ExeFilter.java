package it.brian.utility;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ExeFilter extends FileFilter {
    @Override
    public boolean accept(File f) {

        return f.isDirectory() || "exe".equals(FilenameUtils.getExtension(f.getName()));
    }

    @Override
    public String getDescription() {
        return "Executable files (.exe)";
    }
}
