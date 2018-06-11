package com.milbar.logic.encryption.wrappers;

import com.milbar.logic.abstracts.ArrayDestroyer;
import com.milbar.logic.abstracts.Destroyable;
import com.milbar.logic.exceptions.ImplementationError;
import com.milbar.logic.login.CredentialsManager;

import java.io.Serializable;

public class HashAndSalt implements Serializable, Destroyable {
    
    private byte[] hash;
    private byte[] salt;
    
    public HashAndSalt(byte[] hash, byte[] salt) {
        this.hash = hash;
        this.salt = salt;
    }
    
    public HashAndSalt(String password) throws ImplementationError {
        salt = CredentialsManager.getSalt();
        hash = CredentialsManager.getHash(password, salt);
    }
    
    public byte[] getHash() {
        return hash;
    }
    
    public byte[] getSalt() {
        return salt;
    }
    
    @Override
    public void destroy() {
        ArrayDestroyer.destroy(hash);
        ArrayDestroyer.destroy(salt);
    }
}
