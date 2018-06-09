package com.milbar.gui.helpers;

import com.milbar.logic.encryption.Algorithm;
import com.milbar.logic.encryption.Mode;
import com.milbar.logic.exceptions.UserIsNotLoggedIn;
import com.milbar.logic.login.wrappers.SessionToken;

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
    
    public void getInitialVector(Mode mode, Algorithm algorithm) throws UserIsNotLoggedIn {
        if (sessionToken == null)
            throw new UserIsNotLoggedIn("Cannot return initialize vector without session token.");
        
    }
}
