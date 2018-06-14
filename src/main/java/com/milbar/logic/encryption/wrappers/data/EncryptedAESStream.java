package com.milbar.logic.encryption.wrappers.data;

import com.milbar.logic.encryption.factories.AESKeysFactory;
import com.milbar.logic.exceptions.DecryptionException;
import com.milbar.logic.exceptions.EncryptionException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

public class EncryptedAESStream extends EncryptedStream implements Serializable {
    
    private SecretKeySpec secretKeySpec;
    private IvParameterSpec ivParameterSpec;
    
    EncryptedAESStream(InputStream inputStream, OutputStream outputStream, Cipher cipher, SecretKey secretKey) {
        super(inputStream, outputStream, cipher);
        secretKeySpec = (SecretKeySpec)secretKey;
        byte[] iv = AESKeysFactory.getIv(32);
        this.ivParameterSpec = new IvParameterSpec(iv);
    }
    
    @Override
    void encrypt() throws EncryptionException {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            encryptStream();
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            throw new EncryptionException(e.getMessage());
        }
    }
    
    @Override
    void decrypt() throws DecryptionException {
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            decryptStream();
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            throw new DecryptionException(e.getMessage());
        }
    }
}
