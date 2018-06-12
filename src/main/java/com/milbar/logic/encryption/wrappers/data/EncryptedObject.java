package com.milbar.logic.encryption.wrappers.data;

import com.milbar.logic.encryption.cryptography.Decrypter;
import com.milbar.logic.encryption.cryptography.Encrypter;
import com.milbar.logic.exceptions.DecryptionException;
import com.milbar.logic.exceptions.EncryptionException;

import javax.crypto.Cipher;
import java.io.Serializable;

public abstract class EncryptedObject <E extends Serializable> extends EncryptedData implements Serializable {
    
    private E object;
    
    EncryptedObject(E object, Cipher cipher) {
        super(cipher);
        this.object = object;
    }
    
    abstract void encrypt() throws EncryptionException;
    
    abstract void decrypt() throws DecryptionException;
    
    void encryptObject() throws EncryptionException {
        if (isEncrypted)
            return;
        
        encrypter = new Encrypter(cipher);
        object = encrypter.encrypt(object);
        destroyEncrypter();
        isEncrypted = true;
    }
    
    void decryptObject() throws DecryptionException {
        if (!isEncrypted)
            return;
        
        decrypter = new Decrypter(cipher);
        object = decrypter.decrypt(object);
        destroyDecrypter();
        isEncrypted = false;
    }
    
    public E getObject() {
        return object;
    }
    
}
