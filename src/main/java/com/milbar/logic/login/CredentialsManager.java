package com.milbar.logic.login;

import com.milbar.gui.abstracts.factories.LoggerFactory;
import com.milbar.logic.exceptions.ImplementationError;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CredentialsManager {
    
    Logger logger = LoggerFactory.getLogger(CredentialsManager.class);
    
    private final Random random = new SecureRandom();
    private static final int HASH_ITERATIONS = 100;
    private static final int HASH_LENGTH_IN_BYTES = 128;
    
    /**
     * Returns a random salt to be used to hash a password.
     *
     * @return 16 bytes random salt
     */
    public byte[] getSalt() {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }
    
    public byte[] getHash(final String password, final byte[] salt) throws ImplementationError {
        byte[] hash;
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, HASH_ITERATIONS, HASH_LENGTH_IN_BYTES * 8);
            hash = secretKeyFactory.generateSecret(keySpec).getEncoded();
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new ImplementationError("Unexpected error while creating hash, hashing algorithm not found.");
        } catch (InvalidKeySpecException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new ImplementationError("Unexpected error while creating hash, PBEKeySpec was not valid for chosen algorithm.");
        }
        
        return hash;
    }
    
    public boolean validatePassword(final String password, final byte[] salt, final byte[] hash) {
        byte[] generatedHash;
        
        try {
            generatedHash = getHash(password, salt);
            if (hashEquals(generatedHash, hash)) {
            
            }
        } catch (ImplementationError e) {
            e.printStackTrace();
        }
    }
    
    private boolean hashEquals(byte[] left, byte[] right) {
        if (left.length != right.length)
            return false;
        
        for (int i = 0; i < left.length; i++)
            if (left[i] != right[i])
                return false;
        
        return true;
    }
}