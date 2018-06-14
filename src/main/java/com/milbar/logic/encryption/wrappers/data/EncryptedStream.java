package com.milbar.logic.encryption.wrappers.data;

import com.milbar.logic.encryption.cryptography.Decrypter;
import com.milbar.logic.encryption.cryptography.Encrypter;
import com.milbar.logic.exceptions.DecryptionException;
import com.milbar.logic.exceptions.EncryptionException;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public abstract class EncryptedStream extends EncryptedData implements Serializable {
    
    transient private InputStream inputStream;
    transient private OutputStream outputStream;
    
    EncryptedStream(InputStream inputStream, OutputStream outputStream, Cipher cipher) {
        super(cipher);
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }
    
    void encryptStream() throws EncryptionException {
        if (isEncrypted)
            return;
        
        encrypter = new Encrypter(cipher);
        encrypter.encrypt(inputStream, outputStream);
        destroyEncrypter();
        isEncrypted = true;
    }
    
    void decryptStream() throws DecryptionException {
        if (!isEncrypted)
            return;
        
        decrypter = new Decrypter(cipher);
        decrypter.decrypt(inputStream, outputStream);
        destroyDecrypter();
        isEncrypted = false;
    }
    
    void updateStreams(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }
    
}
