package com.milbar.logic.exceptions;

public class EncryptionException extends Exception {
    
    
    public EncryptionException(String msg) {
        super(msg);
    }
    
    public EncryptionException(Throwable throwable) {
        super(throwable);
    }
}
