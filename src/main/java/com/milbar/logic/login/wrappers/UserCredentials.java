package com.milbar.logic.login.wrappers;

import com.milbar.logic.abstracts.Destroyable;
import com.milbar.logic.encryption.wrappers.HashAndSalt;
import com.milbar.logic.encryption.wrappers.KeyAndSalt;

import java.io.Serializable;
import java.security.spec.KeySpec;

public class UserCredentials implements Destroyable, Serializable {
    
    private String username;
    private HashAndSalt hashAndSalt;
    private KeyAndSalt keyAndSalt;
    
    public UserCredentials(String username, HashAndSalt hashAndSalt, KeyAndSalt keyAndSalt) {
        this.username = username;
        this.hashAndSalt = hashAndSalt;
        this.keyAndSalt = keyAndSalt;
    }
    
    public void destroy() {
        hashAndSalt.destroy();
        keyAndSalt.destroy();
    }
    public String getUsername() {
        return username;
    }
    
    public HashAndSalt getHashAndSalt() {
        return hashAndSalt;
    }
    
    public byte[] getPasswordHash() {
        return hashAndSalt.getHash();
    }
    
    public byte[] getPasswordSalt() {
        return hashAndSalt.getSalt();
    }
    
    public KeyAndSalt getKeyAndSalt() {
        return keyAndSalt;
    }
    
    public byte[] getKeySalt() {
        return keyAndSalt.getSalt();
    }
    
    public KeySpec getKeySpec() {
        return keyAndSalt.getKeySpec();
    }
    
}
