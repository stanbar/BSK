package com.milbar.logic.encryption.wrappers.data;

import com.milbar.logic.exceptions.DecryptionException;
import com.milbar.logic.exceptions.EncryptionException;

import javax.crypto.Cipher;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.KeyPair;

public class EncryptedRSAObject<E extends Serializable> extends EncryptedObject<E> implements Serializable {

    private KeyPair keyPair;

    public EncryptedRSAObject(E object, Cipher cipher, KeyPair keyPair) {
        super(object, cipher);
        this.keyPair = keyPair;
    }
    
    @Override
    public void encrypt() throws EncryptionException {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            encryptObject();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new EncryptionException(e.getMessage());
        }
    }
    
    @Override
    public void decrypt() throws DecryptionException {
        try {
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            decryptObject();
        } catch (InvalidKeyException | DecryptionException e) {
            e.printStackTrace();
            throw new DecryptionException(e.getMessage());
        }
    }
    
}
