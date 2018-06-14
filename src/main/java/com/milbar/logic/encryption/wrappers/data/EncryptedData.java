package com.milbar.logic.encryption.wrappers.data;

import com.milbar.logic.encryption.cryptography.Decrypter;
import com.milbar.logic.encryption.cryptography.Encrypter;
import com.milbar.logic.exceptions.DecryptionException;
import com.milbar.logic.exceptions.EncryptionException;

import javax.crypto.Cipher;
import java.io.Serializable;

public abstract class EncryptedData implements Serializable {
    
    boolean isEncrypted;
    
    transient Cipher cipher;
    transient Encrypter encrypter;
    transient Decrypter decrypter;
    
    EncryptedData(Cipher cipher) {
        this.cipher = cipher;
    }
    
    void destroyEncrypter() {
        encrypter.destroy();
        encrypter = null;
    }
    
    void destroyDecrypter() {
        decrypter.destroy();
        decrypter = null;
    }
    
    public boolean isEncrypted() {
        return isEncrypted;
    }
    
    abstract void encrypt() throws EncryptionException;
    
    abstract void decrypt() throws DecryptionException;
    
}
