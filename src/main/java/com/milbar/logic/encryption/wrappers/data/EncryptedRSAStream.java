package com.milbar.logic.encryption.wrappers.data;

import com.milbar.logic.exceptions.DecryptionException;
import com.milbar.logic.exceptions.EncryptionException;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.KeyPair;

public class EncryptedRSAStream extends EncryptedStream implements Serializable {
    
    private KeyPair keyPair;
    
    EncryptedRSAStream(InputStream inputStream, OutputStream outputStream, Cipher cipher, KeyPair keyPair) {
        super(inputStream, outputStream, cipher);
        this.keyPair = keyPair;
    }
    
    void encrypt() throws EncryptionException {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            encryptStream();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new EncryptionException(e.getMessage());
        }
    }
    
    void decrypt() throws DecryptionException {
        try {
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            decryptStream();
        } catch (InvalidKeyException | DecryptionException e) {
            e.printStackTrace();
            throw new DecryptionException(e.getMessage());
        }
    }
}
