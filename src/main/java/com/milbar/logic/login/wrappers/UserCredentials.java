package com.milbar.logic.login.wrappers;

import java.io.Serializable;

public class UserCredentials implements Destroyable, Serializable {
    
    private String username;
    private byte[] hashedPassword;
    private byte[] salt;
    
    public UserCredentials(String username, byte[] hashedPassword, byte[] salt) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void destroy() {
        username = null;
        hashedPassword = null;
        salt = null;
    }
    
    public byte[] getSalt() {
        return salt;
    }
    
    public byte[] getHashedPassword() {
        return hashedPassword;
    }
}
