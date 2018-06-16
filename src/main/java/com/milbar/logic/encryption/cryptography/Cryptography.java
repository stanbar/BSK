package com.milbar.logic.encryption.cryptography;

import com.milbar.logic.abstracts.EncryptionBlockModeType;
import com.milbar.logic.encryption.factories.AESCipherFactory;
import com.milbar.logic.encryption.factories.AESFactory;
import com.milbar.logic.encryption.factories.RSACipherFactory;
import com.milbar.logic.encryption.factories.RSAFactory;
import com.milbar.logic.encryption.wrappers.data.AESEncryptedObject;
import com.milbar.logic.encryption.wrappers.data.RSAEncryptedObject;
import com.milbar.logic.exceptions.DecryptionException;
import com.milbar.logic.exceptions.EncryptionException;
import org.apache.commons.lang3.SerializationUtils;

import javax.crypto.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class Cryptography implements Encryption, Decryption {
    
    @Override
    public <E extends Serializable> AESEncryptedObject<E> encryptObject(E object, char[] password, EncryptionBlockModeType blockModeType) throws EncryptionException {
        AESFactory aesFactory = new AESFactory();
        AESCipherFactory aesCipherFactory = new AESCipherFactory(aesFactory, blockModeType);
        
        try {
            Cipher cipher = aesCipherFactory.getCipher(password, Cipher.ENCRYPT_MODE);
    
            byte[] serializedObject = SerializationUtils.serialize(object);
            byte[] encryptedObject = cipher.doFinal(serializedObject);
            return new AESEncryptedObject<>(encryptedObject, aesCipherFactory);
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeySpecException exception) {
            throw new EncryptionException(exception);
        }
    }
    
    @Override
    public <E extends Serializable> RSAEncryptedObject<E> encryptObject(E object, PublicKey publicKey) throws EncryptionException {
        RSAFactory rsaFactory = new RSAFactory();
        RSACipherFactory rsaCipherFactory = new RSACipherFactory(rsaFactory);
        try {
            Cipher cipher = rsaCipherFactory.getEncryptCipher(publicKey);
    
            byte[] serializedObject = SerializationUtils.serialize(object);
            byte[] encryptedObject = cipher.doFinal(serializedObject);
            return new RSAEncryptedObject<>(encryptedObject, rsaCipherFactory);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException exception) {
            throw new EncryptionException(exception);
        }
    }
    
    @Override
    public void encryptStream(InputStream inputStream, OutputStream outputStream, char[] password, EncryptionBlockModeType blockModeType) throws EncryptionException {
        AESFactory aesAlgorithmData = new AESFactory();
        AESCipherFactory AESCipherData = new AESCipherFactory(aesAlgorithmData, blockModeType);
        
        try {
            Cipher cipher = AESCipherData.getCipher(password, Cipher.ENCRYPT_MODE);
    
            try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
                CipherHeaderManager.writeCipherData(AESCipherData, outputStream);
                inputStream.transferTo(cipherOutputStream);
            }
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeySpecException exception) {
            throw new EncryptionException(exception);
        }
    }
    
    @Override
    public void encryptStream(InputStream inputStream, OutputStream outputStream, PublicKey publicKey) throws EncryptionException {
        RSACipherFactory cipherFactory = new RSACipherFactory(new RSAFactory());
        try {
            Cipher cipher = cipherFactory.getEncryptCipher(publicKey);
            try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
                inputStream.transferTo(cipherOutputStream);
            }
        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new EncryptionException(e);
        }
    }
    
    @Override
    public <E extends Serializable> E decryptObject(AESEncryptedObject<E> object, char[] password) throws DecryptionException {
        if (!object.isEncrypted())
            throw new DecryptionException("Given object is not encrypted.");
    
        AESCipherFactory cipherFactory = object.getCipherFactory();
        try {
            Cipher cipher = cipherFactory.getCipher(password, Cipher.DECRYPT_MODE);
    
            byte[] decryptedObject = cipher.doFinal(object.getSerializedObject());
            return object.getOriginalObject(decryptedObject);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException exception) {
            throw new DecryptionException(exception);
        }
    }
    
    @Override
    public <E extends Serializable> E decryptObject(RSAEncryptedObject<E> object, PrivateKey privateKey) throws DecryptionException {
        if(!object.isEncrypted())
            throw new DecryptionException("Given object is not encrypted.");
    
        RSACipherFactory cipherFactory = object.getCipherFactory();
        try {
            Cipher cipher = cipherFactory.getDecryptCipher(privateKey);
    
            byte[] decryptedObject = cipher.doFinal(object.getSerializedObject());
            return object.getOriginalObject(decryptedObject);
        } catch (NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException exception) {
            throw new DecryptionException(exception);
        }
    }
    
    @Override
    public void decryptStream(InputStream inputStream, OutputStream outputStream, char[] password) throws DecryptionException {
        try {
            AESCipherFactory aesCipherFactory = CipherHeaderManager.readCipherData(inputStream);
            Cipher cipher = aesCipherFactory.getCipher(password, Cipher.DECRYPT_MODE);
    
            try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
                inputStream.transferTo(cipherOutputStream);
            }
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeySpecException exception) {
            throw new DecryptionException(exception);
        }
    }
    
    @Override
    public void decryptStream(InputStream inputStream, OutputStream outputStream, PrivateKey privateKey) throws DecryptionException {
        try {
            RSACipherFactory rsaCipherFactory = CipherHeaderManager.readCipherData(inputStream);
            Cipher cipher = rsaCipherFactory.getDecryptCipher(privateKey);
    
            try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
                inputStream.transferTo(cipherOutputStream);
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException exception) {
            throw new DecryptionException(exception);
        }
    }
}
