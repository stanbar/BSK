package com.milbar.logic.encryption.wrappers;

import com.milbar.logic.abstracts.Destroyable;

import java.io.*;

public class EncryptedObject <E extends Serializable> implements Serializable, Destroyable {
    
    private E encryptionObject;
    
    private KeyAndSalt keyAndSalt;
    
    public EncryptedObject(KeyAndSalt keyAndSalt, E encryptionObject) {
        this.keyAndSalt = keyAndSalt;
        this.encryptionObject = encryptionObject;
        
    }
    
    public I
    
    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream();
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
    
        objectOutputStream.writeObject(encryptionObject);
        return objectOutputStream;
    }
    
    @Override
    public void destroy() {
        keyAndSalt.destroy();
    }
    
}
