package com.milbar.logic.encryption.wrappers.data;

import com.milbar.logic.exceptions.DecryptionException;
import com.milbar.logic.exceptions.EncryptionException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

public class EncryptedAESObject<E extends Serializable> extends EncryptedObject<E> implements Serializable {
    
    private IvParameterSpec ivParameterSpec;
    
    private transient SecretKeySpec secretKeySpec;
    
    EncryptedAESObject(E object, IvParameterSpec ivParameterSpec, Cipher cipher, SecretKey secretKey) {
        super(object, cipher);
        this.ivParameterSpec = ivParameterSpec;
        secretKeySpec = (SecretKeySpec)secretKey;
    }
    
    public void encrypt() throws EncryptionException {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            encryptObject();
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
            throw new EncryptionException(e.getMessage());
        }
    }
    
    public void decrypt() throws DecryptionException {
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            decryptObject();
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            throw new DecryptionException(e.getMessage());
        }
    }
    
    
}
