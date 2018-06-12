package com.milbar.logic.encryption.wrappers;

import com.milbar.logic.abstracts.ArrayDestroyer;
import com.milbar.logic.abstracts.Destroyable;

import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;

public class KeyAndSalt implements Destroyable, Serializable {
    
    private SecretKeySpec secretKeySpec;
    private byte[] salt;
    private char[] keyPassword;
    
    
    public KeyAndSalt(SecretKeySpec secretKeySpec, String password, byte[] salt) {
        this.secretKeySpec = secretKeySpec;
        this.salt = salt;
        keyPassword = password.toCharArray();
    }
    
    @Override
    public void destroy() {
        ArrayDestroyer.destroy(salt);
        ArrayDestroyer.destroy(keyPassword);
        secretKeySpec = null;
        keyPassword = null;
        salt = null;
    }
    
    public SecretKeySpec getSecretKeySpec() {
        return secretKeySpec;
    }
    
    public byte[] getSalt() {
        return salt;
    }
}
