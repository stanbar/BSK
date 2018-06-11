package com.milbar.logic.encryption.wrappers;

import com.milbar.logic.abstracts.Destroyable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class EncryptedData implements Destroyable {
    
    KeyAndSalt keyAndSalt;
    
    public EncryptedData(KeyAndSalt keyAndSalt) {
        this.keyAndSalt = keyAndSalt;
    }
    
    @Override
    public void destroy() {
       keyAndSalt.destroy();
    }
    
    public abstract InputStream getInputStream() throws IOException;
    
    public abstract OutputStream getOutputStream() throws IOException;
    
    public KeyAndSalt getKeyAndSalt() {
        return keyAndSalt;
    }
    
}
