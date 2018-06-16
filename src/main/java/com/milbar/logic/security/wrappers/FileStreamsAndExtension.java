package com.milbar.logic.security.wrappers;

import java.io.InputStream;
import java.io.OutputStream;

public class FileStreamsAndExtension {
    
    private InputStream inputStream;
    private OutputStream outputStream;
    
    private String fileExtension;
    private String newName;
    
    public FileStreamsAndExtension(InputStream inputStream, OutputStream outputStream, String fileExtension, String newName) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.fileExtension = fileExtension;
        this.newName = newName;
    }
    
    public FileStreamsAndExtension(InputStream inputStream, OutputStream outputStream, String fileExtension) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.fileExtension = fileExtension;
        this.newName = "";
    }
    
    public InputStream getInputStream() {
        return inputStream;
    }
    
    public OutputStream getOutputStream() {
        return outputStream;
    }
    
    public String getFileExtension() {
        return fileExtension;
    }
    
    public String getNewName() {
        return newName;
    }
}
