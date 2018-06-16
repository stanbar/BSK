package com.milbar.logic.encryption.wrappers.data;

import com.milbar.logic.encryption.factories.AESCipherFactory;

import java.io.Serializable;

public class AESEncryptedObject<E extends Serializable> extends EncryptedObject<E> implements Serializable {
    
    
    public AESEncryptedObject(byte[] serializedObject, AESCipherFactory cipherFactory) {
        super(serializedObject, cipherFactory, true);
    }
    
    @Override
    public AESCipherFactory getCipherFactory() {
        return (AESCipherFactory) cipherFactory;
    }
    
}
