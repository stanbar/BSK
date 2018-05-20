package com.milbar.gui.configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class ApplicationConfiguration {
    
    private static String APPLICATION_PATH = initializeApplicationPath();
    private static String DATA_PATH = "data";
    private static String USERS_DATA_FILENAME = "users.dat";
    private static Path USERS_DATA_PATH = Paths.get(APPLICATION_PATH, DATA_PATH, USERS_DATA_FILENAME);
    
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
}
