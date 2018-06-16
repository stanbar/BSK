package com.milbar.logic.security;

import com.milbar.logic.abstracts.Mode;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileForEncryption extends FileCryptography {
    
    private Mode blockModeType;
    
    private String encryptedFileName;
    
    public FileForEncryption(File fileInput, String encryptedFileName, Mode blockModeType) {
        super(fileInput, prepareFileOutput(fileInput, encryptedFileName), getFileExtension(fileInput.getName()), true);
        this.encryptedFileName = encryptedFileName;
        this.blockModeType = blockModeType;
        this.fileExtension = getFileExtension(fileInput.getName());
    }
    
    private static File prepareFileOutput(File fileInput, String encryptedFileName) {
        String parentPath = fileInput.getParentFile().getName();
        Path fileOutputPath = Paths.get(parentPath, encryptedFileName);
        return fileOutputPath.toFile();
    }
    
    private static String getFileExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0)
            extension = fileName.substring(i + i);
        return extension;
    }
    
    public Mode getBlockModeType() {
        return blockModeType;
    }
    
    
    public String getEncryptedFileName() {
        return encryptedFileName;
    }
}
