package com.milbar.logic.encryption;

import com.milbar.logic.abstracts.EncryptionBlockModeType;
import com.milbar.logic.encryption.wrappers.EncryptedData;
import com.milbar.logic.encryption.wrappers.EncryptedFile;
import com.milbar.logic.exceptions.EncryptionException;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class EncryptionManager {
    
    private EncryptedData encryptedData;
    private Cipher cipher;
    private SecretKeySpec encryptionKey;
    
    private EncryptionManager(EncryptedFile encryptedFile, EncryptionBlockModeType blockModeType) {
        try {
            this.encryptedData = encryptedFile;
            cipher = Cipher.getInstance("AES/" + blockModeType.fullName + "/PKCS5Padding");
            setupKeys();
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }
    
    public void encrypt() throws EncryptionException, InvalidKeyException {
        try (InputStream fileInputStream = encryptedData.getInputStream();
             OutputStream fileOutputStream = encryptedData.getOutputStream()) {
            
            encryptStream(fileInputStream, fileOutputStream);
            
        } catch (IOException e) {
            e.printStackTrace();
            throw new EncryptionException(e.getMessage());
        }
    }
    
    public void decrypt() throws EncryptionException, InvalidKeyException {
        cipher.init(Cipher.DECRYPT_MODE, encryptionKey);
        
        try (InputStream fileInputStream = encryptedData.getInputStream();
             OutputStream fileOutputStream = encryptedData.getOutputStream()) {
            
            decryptStream(fileInputStream, fileOutputStream);
            
        } catch (IOException e) {
            e.printStackTrace();
            throw new EncryptionException(e.getMessage());
        }
    }
    
    public void encryptStream(InputStream inputStream, OutputStream outputStream) throws InvalidKeyException, IOException {
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
        
        try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
            inputStream.transferTo(cipherOutputStream);
        }
    }
    
    public void decryptStream(InputStream inputStream, OutputStream outputStream) throws IOException, InvalidKeyException {
        cipher.init(Cipher.DECRYPT_MODE, encryptionKey);
    
        try (CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher)) {
            cipherInputStream.transferTo(outputStream);
        }
    }
    
    private void setupKeys() throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        KeySpec keySpec = encryptedData.getKeyAndSalt().getKeySpec();
        SecretKey temp = factory.generateSecret(keySpec);
        encryptionKey = new SecretKeySpec(temp.getEncoded(), "AES");
    }
}
