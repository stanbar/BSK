package com.milbar.logic.login;

import com.milbar.gui.abstracts.factories.LoggerFactory;
import com.milbar.logic.abstracts.Mode;
import com.milbar.logic.encryption.cryptography.ObjectCryptography;
import com.milbar.logic.encryption.wrappers.data.AESEncryptedObject;
import com.milbar.logic.exceptions.*;
import com.milbar.logic.login.loaders.SerializedDataReader;
import com.milbar.logic.login.wrappers.SessionToken;
import com.milbar.logic.login.wrappers.UserCredentials;
import com.milbar.logic.security.wrappers.Password;

import java.nio.file.Path;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsersManager {
    
    private final static Logger logger = LoggerFactory.getLogger(UsersManager.class);
    
    private ObjectCryptography objectCryptography = new ObjectCryptography();
    
    private SerializedDataReader<String, AESEncryptedObject<UserCredentials>> usersCollection;
    private SerializedDataReader<String, PublicKey> usersPublicKeys;
    
    UsersManager(Path pathToUsersData, Path pathToUsersPublicKeys) throws InstanceInitializeException {
        initializeUsersCollection(pathToUsersData);
        initializeUsersPublicKeys(pathToUsersPublicKeys);
    }
    
    private void initializeUsersCollection(Path pathToUsersData) throws InstanceInitializeException {
        usersCollection = new SerializedDataReader<>(pathToUsersData);

        try {
            usersCollection.readFromFile();
        } catch (ReadingSerializedFileException e) {
            try {
                Map<String, AESEncryptedObject<UserCredentials>> usersList = new HashMap<>();
                usersCollection.updateCollection(usersList);
                logger.log(Level.WARNING, "Failed to load users data from file. Creating a new, empty collection.");
            } catch (WritingSerializedFileException writeException) {
                logger.log(Level.SEVERE, "Failed to update users collection.");
                throw new InstanceInitializeException("Unable to create empty users collection.");
            }
        }
    }
    
    private void initializeUsersPublicKeys(Path pathToUsersPublicKeys) throws InstanceInitializeException {
        usersPublicKeys = new SerializedDataReader<>(pathToUsersPublicKeys);
        
        try {
            usersPublicKeys.readFromFile();
        } catch (ReadingSerializedFileException e) {
            try {
                Map<String, PublicKey> publicKeysList = new HashMap<>();
                usersPublicKeys.updateCollection(publicKeysList);
                logger.log(Level.WARNING, "Failed to load users public keys from file. Creating a new, empty collection.");
            } catch (WritingSerializedFileException writeException) {
                logger.log(Level.SEVERE, "Failed to update users public keys collection.");
                throw new InstanceInitializeException("Unable to create empty users public keys collection.");
            }
        }
    }
    
    void registerUser(String username, Password password) throws UserAlreadyExists, RegisterException {
        if (usersCollection.keyExists(username))
            throw new UserAlreadyExists("User with name: " + username + " already exists.");
        
        try {
            UserCredentials userCredentials = new UserCredentials(username);
            AESEncryptedObject<UserCredentials> encryptedCredentials;
            encryptedCredentials = objectCryptography.encryptObject(userCredentials, password, Mode.CBC);
            usersCollection.updateCollection(username, encryptedCredentials);
        } catch (EncryptionException | WritingSerializedFileException e) {
            throw new RegisterException("Failed to register a new user.");
        }
    }
    
    void removeUser(String username, Password password) throws UserRemoveException {
        AESEncryptedObject<UserCredentials> usersCredentials = usersCollection.getValue(username);
        if (usersCredentials == null)
            throw new UserRemoveException("Failed to remove user, because there is not user with name: " + username);
        
        try {
            UserCredentials decryptedUserCredentials = objectCryptography.decryptObject(usersCredentials, password);
            if (decryptedUserCredentials.getUsername().equals(username)) {
                usersCollection.removeItem(username);
            }
        } catch (DecryptionException e) {
            throw new UserRemoveException("Given password does not match.");
        } catch (WritingSerializedFileException e) {
            throw new UserRemoveException("Failed to remove user from collection.");
        }
    }

    SessionToken loginUser(String username, Password password) throws UserDoesNotExist, UsersPasswordNotValid {
        AESEncryptedObject<UserCredentials> userCredentials = usersCollection.getValue(username);
        if (usersCollection == null)
            throw new UserDoesNotExist("User with name: " + username + " does not exists.");
        
        try {
            UserCredentials decryptedUserCredentials = objectCryptography.decryptObject(userCredentials, password);
            return new SessionToken(decryptedUserCredentials);
        } catch (DecryptionException e) {
            throw new UsersPasswordNotValid("Failed to login as user " + username + ", password is not valid.");
        }
    }
    
    public List<String> getUsersList() {
        return usersCollection.getKeysList();
    }
}
