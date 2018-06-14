package com.milbar.logic.encryption.wrappers;

import com.milbar.logic.abstracts.ArrayDestroyer;
import com.milbar.logic.abstracts.Destroyable;

import javax.crypto.SecretKey;
import java.io.Serializable;

public class KeyAndSalt implements Destroyable, Serializable {
    
    private SecretKey secretKey;
    private byte[] salt;
    private char[] keyPassword;
    
    
    public KeyAndSalt(SecretKey secretKey, String password, byte[] salt) {
        this.secretKey = secretKey;
        this.salt = salt;
        keyPassword = password.toCharArray();
    }
    
    @Override
    public void destroy() {
        ArrayDestroyer.destroy(salt);
        ArrayDestroyer.destroy(keyPassword);
        secretKey = null;
        keyPassword = null;
        salt = null;
    }
    
    public SecretKey getSecretKeySpec() {
        return secretKey;
    }
    
    public byte[] getSalt() {
        return salt;
    }
}
