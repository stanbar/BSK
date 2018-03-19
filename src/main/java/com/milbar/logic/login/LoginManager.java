package com.milbar.logic.login;

import com.milbar.logic.exceptions.LoginException;

public class LoginManager {
    
    
    public UserCredentials getUserCredentials(String username, String password) throws LoginException {
        // todo implement login logic
        return new UserCredentials(username, password);
    }
}
