package com.milbar.logic.encryption.wrappers;

import com.milbar.logic.abstracts.Destroyable;

import java.io.*;

public class EncryptedFile extends EncryptedData implements Serializable, Destroyable {
    
    private File fileInput, fileOutput;
    
    public EncryptedFile(KeyAndSalt keyAndSalt, File fileInput, File fileOutput) {
        super(keyAndSalt);
        this.fileInput = fileInput;
        this.fileOutput = fileOutput;
    }
    
    public InputStream getInputStream() throws FileNotFoundException {
        return new FileInputStream(fileInput);
    }
    
    public OutputStream getOutputStream() throws FileNotFoundException {
        return new FileOutputStream(fileOutput);
    }
    
}
