package com.milbar.logic.login;

import com.milbar.gui.configuration.ApplicationConfiguration;
import com.milbar.logic.exceptions.LoginException;
import com.milbar.logic.exceptions.UserDoesNotExist;
import com.milbar.logic.exceptions.UserIsNotLoggedIn;
import com.milbar.logic.exceptions.UsersPasswordNotValid;
import com.milbar.logic.login.wrappers.SessionToken;

import java.nio.file.Path;
import java.util.Date;

public class LoginManager {
    
    private final static Path USERS_DATA_PATH = ApplicationConfiguration.getUsersDataPath();
    private final static long SESSION_LENGTH = ApplicationConfiguration.getSessionLength();
    
    private CredentialsManager credentialsManager = new CredentialsManager();
    private UsersManager usersManager = new UsersManager(USERS_DATA_PATH);
    
    
    private SessionToken usersSession;
    private Date sessionValidUntilDate;
    
    public void login(String username, String password) throws LoginException {
        logout();
        
        try {
            usersSession = usersManager.loginUser(username, password);
            Date currentDate = new Date();
            sessionValidUntilDate = new Date(currentDate.getTime() + SESSION_LENGTH);
        } catch (UserDoesNotExist | UsersPasswordNotValid e) {
            throw new LoginException(e.getMessage());
        }
    }
    
    public void logout() {
        if (!isLoggedIn())
            return;
        
        usersSession = null;
        sessionValidUntilDate = null;
    }
    
    public void refreshSession() throws UserIsNotLoggedIn {
        if (isLoggedIn())
            throw new UserIsNotLoggedIn("Cannot refresh current session, because there is not user logged in.");
        
        Date currentDate = new Date();
        sessionValidUntilDate.setTime(currentDate.getTime() + SESSION_LENGTH);
    }
    
    public boolean isSessionValid() {
        if (!isLoggedIn())
            return false;
        
        Date currentDate = new Date();
        return currentDate.getTime() <= sessionValidUntilDate.getTime();
    }
    
    private boolean isLoggedIn() {
        return usersSession != null && sessionValidUntilDate != null;
    }
}
