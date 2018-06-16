package com.milbar.logic.security;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileForDecryption extends FileCryptography {
    
    public FileForDecryption(File fileInput, String newName, String fileExtension) {
        super(fileInput, prepareFileOutput(fileInput, newName, fileExtension), fileExtension, false);
    }
    
    private static File prepareFileOutput(File fileInput, String newName, String fileExtension) {
        String parentPath = fileInput.getParentFile().getName();
        Path outputPath = Paths.get(parentPath, newName + fileExtension);
        return outputPath.toFile();
    }
    
    public FileForDecryption(File fileInput, String fileExtension) {
        super(fileInput, prepareFileOutput(fileInput, fileExtension), fileExtension, false);
    }
    
    private static File prepareFileOutput(File fileInput, String fileExtension) {
        String baseName = FilenameUtils.getBaseName(fileInput.getAbsolutePath());
        Path path = Paths.get(fileInput.getParentFile().getAbsolutePath(), baseName + fileExtension);
        return path.toFile();
    }
    
    
}
