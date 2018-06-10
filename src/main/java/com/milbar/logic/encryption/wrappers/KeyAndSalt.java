package com.milbar.logic.encryption.wrappers;

import com.milbar.logic.abstracts.Destroyable;
import com.milbar.logic.exceptions.EncryptionException;
import com.milbar.logic.login.CredentialsManager;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;

public class KeyAndSalt implements Destroyable, Serializable {
    
    private KeySpec keySpec;
    private SecretKey encryptionKey;
    private byte[] salt;
    private char[] keyPassword;
    
    private transient Cipher cipher;
    private transient AlgorithmParameters algorithmParameters;
    
    public KeyAndSalt() {
        try {
            byte[] randomByteArray = CredentialsManager.getSalt(64);
            keyPassword = new String(randomByteArray).toCharArray();
            setupKeys();
            setupCipher();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException e) {
            destroy();
            e.printStackTrace();
        }
    }
    
    public KeyAndSalt(String password) {
        try {
            keyPassword = password.toCharArray();
            setupKeys();
            setupCipher();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException e) {
            destroy();
            e.printStackTrace();
        }
    }
    
    private void setupCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        algorithmParameters = cipher.getParameters();
    }
    
    private void prepareForEncryption() throws InvalidKeyException {
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
    }
    
    private void prepareForDecryption() throws InvalidKeyException {
        cipher.init(Cipher.DECRYPT_MODE, encryptionKey);
    }
    
    private void setupKeys() throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        salt = CredentialsManager.getSalt(new SecureRandom());
        keySpec = new PBEKeySpec(keyPassword, salt, 131072, 512);
        SecretKey temp = factory.generateSecret(keySpec);
        encryptionKey = new SecretKeySpec(temp.getEncoded(), "AES");
    }
    
    @Override
    public void destroy() {
        keySpec = null;
        encryptionKey = null;
        keyPassword = null;
        salt = null;
    }
    
    public EncryptedData encrypt(byte[] data) throws EncryptionException {
        try{
            byte[] iv = algorithmParameters.getParameterSpec(IvParameterSpec.class).getIV();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            byte[] encryptedRawData = cipher.doFinal(data);
            return new EncryptedData(ivParameterSpec, encryptedRawData);
            
        } catch (InvalidParameterSpecException | BadPaddingException | IllegalBlockSizeException e) {
            throw new EncryptionException(e.getMessage());
        }
    }
    
    public KeySpec getKeySpec() {
        return keySpec;
    }
    
    public byte[] getSalt() {
        return salt;
    }
}
