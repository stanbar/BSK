package com.milbar.gui.configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class ApplicationConfiguration {
    
    private static String APPLICATION_PATH = initializeApplicationPath();
    private static String DATA_PATH = "data";
    private static String USERS_DATA_FILENAME = "users.dat";
    private static String PUBLIC_KEYS_FILENAME = "usersPublicKeys.dat";
    private static Path USERS_DATA_PATH = Paths.get(APPLICATION_PATH, DATA_PATH, USERS_DATA_FILENAME);
    private static Path USERS_PUBLIC_KEYS_PATH = Paths.get(APPLICATION_PATH, DATA_PATH, PUBLIC_KEYS_FILENAME);
    
    private static String USERNAME_REPLACE_CONSTANT = "&USERNAME&";
    private static String ALL_USER_DATA_DIRECTORY = "users";
    private static String SINGLE_USER_DATA_FILENAME = USERNAME_REPLACE_CONSTANT + ".dat";
    private static Path SINGLE_USER_DATA_PATH = Paths.get(APPLICATION_PATH, ALL_USER_DATA_DIRECTORY);
    
    // in milliseconds
    private static long SESSION_LENGTH = 3600000;
    
    private ApplicationConfiguration() {
    
    }
    
    public static boolean load() {
        //todo load logic
        return false;
    }
    
    public static String getApplicationPath() {
        return APPLICATION_PATH;
    }
    
    public static String getUsersDataFilename() {
        return USERS_DATA_FILENAME;
    }
    
    private static String initializeApplicationPath() {
        Path currentWorkingDir = Paths.get("").toAbsolutePath();
        return currentWorkingDir.normalize().toString();
    }
    
    public static String getDataPath() {
        return DATA_PATH;
    }
    
    public static Path getUsersDataPath() {
        return USERS_DATA_PATH;
    }
    
    public static long getSessionLength() {
        return SESSION_LENGTH;
    }
    
    public static Path getUsersPublicKeysPath() {
        return USERS_PUBLIC_KEYS_PATH;
    }
    
    public static Path getSingleUserDataPath(String username) {
        String singleUserDataFilename = SINGLE_USER_DATA_FILENAME;
        String users = singleUserDataFilename.replace(USERNAME_REPLACE_CONSTANT, username);
        return SINGLE_USER_DATA_PATH;
    }
    
    public static void setSingleUserDataPath(Path singleUserDataPath) {
        SINGLE_USER_DATA_PATH = singleUserDataPath;
    }
}
