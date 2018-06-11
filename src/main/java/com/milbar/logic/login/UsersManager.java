package com.milbar.logic.login;

import com.milbar.gui.abstracts.factories.LoggerFactory;
import com.milbar.logic.encryption.wrappers.HashAndSalt;
import com.milbar.logic.encryption.wrappers.KeyAndSalt;
import com.milbar.logic.exceptions.*;
import com.milbar.logic.login.loaders.SerializedDataReader;
import com.milbar.logic.login.wrappers.SessionToken;
import com.milbar.logic.login.wrappers.UserCredentials;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsersManager {
    
    private final static Logger logger = LoggerFactory.getLogger(UsersManager.class);
    
    private SerializedDataReader<String, EncryptedUserCredentials> usersCollection;
    
    UsersManager(Path pathToUsersData) {
        usersCollection = new SerializedDataReader<>(pathToUsersData);
        try {
            usersCollection.readFromFile();
        } catch (ReadingSerializedFileException e) {
            try {
                Map<String, UserCredentials> usersList = new HashMap<>();
                usersCollection.updateCollection(usersList);
                logger.log(Level.WARNING, "Failed to load users data from file. Creating a new, empty collection.");
            } catch (WritingSerializedFileException writeException) {
                writeException.printStackTrace();
                logger.log(Level.SEVERE, "Failed to update users collection.");
            }
        }
    }
    
    boolean registerUser(String username, String password) throws UserAlreadyExists {
        if (usersCollection.keyExists(username))
            throw new UserAlreadyExists("User with name: " + username + " already exists.");
        
        try {
            HashAndSalt hashAndSalt = new HashAndSalt(password);
            KeyAndSalt keyAndSalt = new KeyAndSalt();
            UserCredentials newUser = new UserCredentials(username, hashAndSalt, keyAndSalt);
            usersCollection.updateCollection(username, newUser);
        } catch (ImplementationError | WritingSerializedFileException e) {
            e.printStackTrace();
            return false;
        }
    
        return true;
    }
    
    boolean removeUser(String username, String password) throws UserDoesNotExist {
        UserCredentials usersCredentials = usersCollection.getValue(username);
        if (usersCredentials == null)
            throw new UserDoesNotExist("Failed to remove user, because there is not user with name: " + username);
        
        byte[] salt = usersCredentials.getPasswordSalt();
        byte[] hashedPassword = usersCredentials.getPasswordHash();
        if (CredentialsManager.validatePassword(password, salt, hashedPassword)) {
            try {
                usersCollection.removeItem(username);
            } catch (WritingSerializedFileException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        else
            return false;
    }

    SessionToken loginUser(String username, String password) throws UserDoesNotExist, UsersPasswordNotValid {
        UserCredentials userCredentials = usersCollection.getValue(username);
        if (usersCollection == null)
            throw new UserDoesNotExist("User with name: " + username + " does not exists.");
        
        byte[] salt = userCredentials.getPasswordSalt();
        byte[] hashedPassword = userCredentials.getPasswordHash();
        if (CredentialsManager.validatePassword(password, salt, hashedPassword))
            return new SessionToken(username, userCredentials.getKeyAndSalt());
        else
            throw new UsersPasswordNotValid("Login operation for " + username + " failed. Given password is wrong.");
    }
    
    public List<String> getUsersList() {
        Map<String, UserCredentials> userCredentials = usersCollection.getCollection();
        return new ArrayList<>(userCredentials.keySet());
    }
}
