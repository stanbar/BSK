package com.milbar.logic.login.wrappers;

import com.milbar.gui.configuration.ApplicationConfiguration;
import com.milbar.logic.abstracts.Destroyable;
import com.milbar.logic.encryption.wrappers.KeyAndSalt;

import java.util.Date;

public class SessionToken implements Destroyable {
    
    private final static long SESSION_LENGTH = ApplicationConfiguration.getSessionLength();
    
    private String username;
    private KeyAndSalt usersEncryptionKey;
    private Date sessionValidUntil;
    
    public SessionToken(String username, KeyAndSalt usersEncryptionKey) {
        this.username = username;
        this.usersEncryptionKey = usersEncryptionKey;
        refresh();
    }
    
    public String getUsername() {
        return username;
    }
    
    public KeyAndSalt getUsersEncryptionKey() {
        return usersEncryptionKey;
    }
    
    public Date getValidDate() {
        return sessionValidUntil;
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
        usersEncryptionKey.destroy();
        this.username = null;
    }
}
