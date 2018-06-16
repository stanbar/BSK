package com.milbar.logic.security;

import java.io.File;

public abstract class FileCryptography {
    
    
    File fileInput;
    File fileOutput;
    String fileExtension;
    
    boolean encryption;
    
    public FileCryptography(File fileInput, File fileOutput, String fileExtension, boolean encryption) {
        this.fileInput = fileInput;
        this.fileOutput = fileOutput;
        this.fileExtension = fileExtension;
        this.encryption = encryption;
    }
    
    public File getFileInput() {
        return fileInput;
    }
    
    public File getFileOutput() {
        return fileOutput;
    }
    
    public String getFileExtension() {
        return fileExtension;
    }
    
    public boolean isEncryption() {
        return encryption;
    }
}
