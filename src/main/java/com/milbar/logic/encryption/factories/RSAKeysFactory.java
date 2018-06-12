package com.milbar.logic.encryption.factories;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public abstract class RSAKeysFactory {
    
    private final static String ALGORITHM_NAME = "RSA";
    private final static int KEY_SIZE = 2048;
    
    private static KeyPairGenerator keyPairGenerator;
    
    static {
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_NAME);
            keyPairGenerator.initialize(KEY_SIZE);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
    
    private RSAKeysFactory() {
    
    }
    
    public KeyPair getKeyPair() {
        return keyPairGenerator.generateKeyPair();
    }
    
    public Cipher getCipher() {
        try {
            return Cipher.getInstance(ALGORITHM_NAME);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
    
}
