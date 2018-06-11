package com.milbar.logic.encryption.wrappers;

import com.milbar.logic.abstracts.ArrayDestroyer;
import com.milbar.logic.abstracts.Destroyable;
import com.milbar.logic.login.CredentialsManager;

import java.io.Serializable;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

public class KeyAndSalt implements Destroyable, Serializable {
    
    private KeySpec keySpec;
    private byte[] salt;
    private char[] keyPassword;
    
    public KeyAndSalt() {
        byte[] randomByteArray = CredentialsManager.getSalt(64);
        keyPassword = new String(randomByteArray).toCharArray();
        salt = CredentialsManager.getSalt(new SecureRandom());
    }
    
    public KeyAndSalt(String password) {
        keyPassword = password.toCharArray();
        salt = CredentialsManager.getSalt(new SecureRandom());
    }
    
    @Override
    public void destroy() {
        ArrayDestroyer.destroy(salt);
        ArrayDestroyer.destroy(keyPassword);
        keySpec = null;
        keyPassword = null;
        salt = null;
    }
    
    public KeySpec getKeySpec() {
        return keySpec;
    }
    
    public byte[] getSalt() {
        return salt;
    }
}
