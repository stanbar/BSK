package com.milbar.logic.login.wrappers;

import com.milbar.gui.configuration.ApplicationConfiguration;

import java.util.Arrays;
import java.util.Date;

public class SessionToken implements Destroyable {
    
    private final static long SESSION_LENGTH = ApplicationConfiguration.getSessionLength();
    
    private String username;
    private byte[] token;
    private Date sessionValidUntil;
    
    public SessionToken(String username, byte[] token) {
        this.username = username;
        this.token = token;
        refresh();
    }
    
    public byte[] getToken() {
        return token;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void refresh() {
        Date currentDate = new Date();
        sessionValidUntil = new Date(currentDate.getTime() + SESSION_LENGTH);
    }
    
    public boolean isSessionValid() {
        Date currentDate = new Date();
        return currentDate.getTime() <= sessionValidUntil.getTime();
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        
        if (other instanceof SessionToken) {
            SessionToken otherToken = (SessionToken)other;
            return Arrays.equals(this.token, otherToken.token)
                    && this.username.equals(otherToken.username);
        }
        else
            return false;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + Arrays.hashCode(this.token);
        result = 31 * result + username.hashCode();
        return result;
    }
    
    public void destroy() {
        this.username = null;
        this.token = null;
    }
}
