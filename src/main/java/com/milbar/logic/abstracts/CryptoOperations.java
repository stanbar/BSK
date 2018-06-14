package com.milbar.logic.abstracts;

public enum CryptoOperations {
    
    ENCRYPTION,
    DECRYPTION,
    NONE;
    
    public static boolean swapStreamsNeeded(CryptoOperations lastOperation, CryptoOperations nextOperation) {
        if (lastOperation == nextOperation)
            return false;
        
        return lastOperation != NONE;
    }
    
}
