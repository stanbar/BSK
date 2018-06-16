package com.milbar.logic.login.wrappers;

import com.milbar.gui.configuration.ApplicationConfiguration;
import com.milbar.logic.abstracts.ArrayDestroyer;
import com.milbar.logic.abstracts.Destroyable;

import java.security.SecureRandom;
import java.util.Date;

public class SessionToken implements Destroyable {
    
    private final static long SESSION_LENGTH = ApplicationConfiguration.getSessionLength();
    private final static int SESSION_KEY_LENGTH = 32;
    
    private UserCredentials userCredentials;
    private byte[] sessionKey = new byte[SESSION_KEY_LENGTH];
    private Date sessionValidUntil;
    
    public SessionToken(UserCredentials userCredentials) {
        this.userCredentials = userCredentials;
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(sessionKey);
        refresh();
    }
    
    public UserCredentials getUserCredentials() {
        return userCredentials;
    }
    
    public Date getSessionValidUntil() {
        return sessionValidUntil;
    }
    
    public byte[] getSessionKey() {
        return sessionKey;
    }
    
    public void refresh() {
        Date currentDate = new Date();
        sessionValidUntil = new Date(currentDate.getTime() + SESSION_LENGTH);
    }
    
    public boolean isSessionValid() {
        Date currentDate = new Date();
        return currentDate.getTime() <= sessionValidUntil.getTime();
    }
    
    public void destroy() {
        userCredentials = null;
        ArrayDestroyer.destroy(sessionKey);
        sessionValidUntil = new Date(0);
    }
    
}
