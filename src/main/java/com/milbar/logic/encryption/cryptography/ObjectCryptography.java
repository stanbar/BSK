package com.milbar.logic.encryption.cryptography;

import com.milbar.logic.abstracts.Mode;
import com.milbar.logic.encryption.factories.AESCipherFactory;
import com.milbar.logic.encryption.factories.AESFactory;
import com.milbar.logic.encryption.factories.RSACipherFactory;
import com.milbar.logic.encryption.factories.RSAFactory;
import com.milbar.logic.encryption.wrappers.data.AESEncryptedObject;
import com.milbar.logic.encryption.wrappers.data.RSAEncryptedObject;
import com.milbar.logic.exceptions.DecryptionException;
import com.milbar.logic.exceptions.EncryptionException;
import com.milbar.logic.security.wrappers.Password;
import org.apache.commons.lang3.SerializationUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.Serializable;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class ObjectCryptography implements EncryptionObject, DecryptionObject {
    
    public ObjectCryptography() {
    
    }
    
    @Override
    public <E extends Serializable> AESEncryptedObject<E> encryptObject(E object, Password password, Mode blockModeType) throws EncryptionException {
        AESFactory aesFactory = new AESFactory();
        AESCipherFactory aesCipherFactory = new AESCipherFactory(aesFactory, blockModeType);
        
        try {
            Cipher cipher = aesCipherFactory.getCipher(password.getSecret(), Cipher.ENCRYPT_MODE);
    
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
    public <E extends Serializable> E decryptObject(AESEncryptedObject<E> object, Password password) throws DecryptionException {
        if (!object.isEncrypted())
            throw new DecryptionException("Given object is not encrypted.");
    
        AESCipherFactory cipherFactory = object.getCipherFactory();
        try {
            Cipher cipher = cipherFactory.getCipher(password.getSecret(), Cipher.DECRYPT_MODE);
    
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
    
}
