package com.milbar.gui.helpers;

import com.milbar.logic.login.wrappers.SessionToken;
import com.milbar.logic.login.wrappers.UserCredentials;

public class LoginController {
    
    
    SessionToken sessionToken = null;
    
    public boolean isLoggedIn() {
        return sessionToken != null;
    }
    
    public void login(SessionToken newSessionToken) {
        if (sessionToken != null)
            sessionToken.destroy();
        
        sessionToken = newSessionToken;
    }
    
    public void logout() {
        if (sessionToken != null) {
            sessionToken.destroy();
            sessionToken = null;
        }
    }
    
    public SessionToken getSessionToken() {
        return sessionToken;
    }
    
    public UserCredentials getUserCredentials() {
        if (sessionToken != null)
            return sessionToken.getUserCredentials();
        else
            return null;
    }
    
}
