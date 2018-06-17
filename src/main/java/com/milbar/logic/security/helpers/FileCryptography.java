package com.milbar.logic.security.helpers;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class FileCryptography {
    
    private static File prepareFileOutput(File fileInput, String fileExtension) {
        String baseName = FilenameUtils.getBaseName(fileInput.getAbsolutePath());
        Path path = Paths.get(fileInput.getParentFile().getAbsolutePath(), baseName + fileExtension);
        return path.toFile();
    }
    
    private static String getFileExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0)
            extension = fileName.substring(i + i);
        return extension;
    }
    
}
