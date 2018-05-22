package com.milbar.logic.login;

import com.milbar.gui.configuration.ApplicationConfiguration;
import com.milbar.logic.exceptions.*;
import com.milbar.logic.login.wrappers.SessionToken;

import java.nio.file.Path;
import java.util.List;

public class LoginManager {
    
    private final static Path USERS_DATA_PATH = ApplicationConfiguration.getUsersDataPath();
    
    private UsersManager usersManager = new UsersManager(USERS_DATA_PATH);
    
    private SessionToken usersSession;
    
    public SessionToken login(String username, String password) throws LoginException {
        try {
            logout();
            usersSession = usersManager.loginUser(username, password);
        } catch (UserDoesNotExist | UsersPasswordNotValid e) {
            throw new LoginException(e.getMessage());
        }
        return usersSession;
    }
    
    public void logout() {
        if (usersSession != null) {
            usersSession.destroy();
            usersSession = null;
        }
    }
    
    public void register(String username, String password) throws RegisterException {
        try {
            if (!usersManager.registerUser(username, password))
                throw new RegisterException("Failed to register user.");
        } catch (UserAlreadyExists e) {
            throw new RegisterException(e.getMessage());
        }
    }
    
    public void removeUser(String username, String password) throws RemoveUserException {
        try {
            if (!usersManager.removeUser(username, password))
                throw new RemoveUserException("Failed to remove user.");
        } catch (UserDoesNotExist e) {
            throw new RemoveUserException(e.getMessage());
        }
    }
    
    public List<String> getUsersList() {
        return usersManager.getUsersList();
    }
    
    private boolean isLoggedIn() {
        return usersSession != null && usersSession.isSessionValid();
    }
}
