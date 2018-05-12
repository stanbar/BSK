package com.milbar.gui.abstracts.factories;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class LoggerFactory {
    
    private final static boolean DEBUG = true;
    
    private static Map<String, Logger> loggers = new HashMap<>();
    
    private LoggerFactory() { }
    
    public static Logger getLogger(Class classForLogging) {
        String className = classForLogging.getCanonicalName();
        if (loggers.containsKey(className))
            return loggers.get(className);
        else {
            Logger newLogger = createLogger(className);
            loggers.put(className, newLogger);
            return newLogger;
        }
    }
    
    private static Logger createLogger(String className) {
        Logger newLogger = Logger.getLogger(className);
        if (DEBUG)
            newLogger.setLevel(Level.ALL);
        else
            newLogger.setLevel(Level.SEVERE);
    
        return newLogger;
    }
}
