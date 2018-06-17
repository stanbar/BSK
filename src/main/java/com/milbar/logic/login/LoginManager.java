package com.milbar.logic.login;

import com.milbar.gui.configuration.ApplicationConfiguration;
import com.milbar.logic.exceptions.*;
import com.milbar.logic.login.wrappers.SessionToken;
import com.milbar.logic.security.wrappers.Password;

import java.nio.file.Path;
import java.util.List;

public class LoginManager {
    
    private final static Path USERS_DATA_PATH = ApplicationConfiguration.getUsersDataPath();
    private final static Path USERS_PUBLIC_KEYS_PATH = ApplicationConfiguration.getUsersPublicKeysPath();
    
    private UsersManager usersManager;
    
    private SessionToken usersSession;
    
    public LoginManager() throws InstanceInitializeException {
        usersManager = new UsersManager(USERS_DATA_PATH, USERS_PUBLIC_KEYS_PATH);
    }
    
    public SessionToken login(String username, Password password) throws LoginException {
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
    
    public void register(String username, Password password) throws RegisterException {
        try {
            usersManager.registerUser(username, password);
        } catch (UserAlreadyExists e) {
            throw new RegisterException(e.getMessage());
        }
    }
    
//    public void removeUser(String username, Password password) throws RemoveUserException {
//        try {
//            usersManager.removeUser(username, password);
//        } catch (UserRemoveException e) {
//            throw new RemoveUserException(e.getMessage());
//        }
//    }
    
    public List<String> getUsersList() {
        return usersManager.getUsersList();
    }
    
    private boolean isLoggedIn() {
        return usersSession != null && usersSession.isSessionValid();
    }
}
