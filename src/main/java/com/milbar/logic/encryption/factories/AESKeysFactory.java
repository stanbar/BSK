package com.milbar.logic.encryption.factories;

import com.milbar.logic.abstracts.BlockModes;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public abstract class AESKeysFactory {
    
    private final static String ALGORITHM_NAME = "PBKDF2WithHmacSHA512";
    private static final int HASH_ITERATIONS = 100;
    private static final int HASH_LENGTH_IN_BYTES = 128;
    
    private static SecretKeyFactory factory;
    
    static {
        try {
            factory = SecretKeyFactory.getInstance(ALGORITHM_NAME);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
    
    private AESKeysFactory() {
    
    }
    
    public static SecretKey getSecretKey(String password, byte[] salt) {
        try {
            PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, HASH_ITERATIONS, HASH_LENGTH_IN_BYTES * 8);
            return factory.generateSecret(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public static SecretKey getSecretKey(KeySpec keySpec) throws IllegalArgumentException {
        if (!(keySpec instanceof PBEKeySpec))
            throw new IllegalArgumentException("Given KeySpec is not instance of PBEKeySpec.");
        
        try {
            return factory.generateSecret(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public static Cipher getCipher(BlockModes blockModes) {
        try {
            return Cipher.getInstance("AES/" + blockModes.shortName + "/PKCS7Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public static byte[] getIv(int lengthInBytes) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[lengthInBytes];
        secureRandom.nextBytes(iv);
        return iv;
    }
}
