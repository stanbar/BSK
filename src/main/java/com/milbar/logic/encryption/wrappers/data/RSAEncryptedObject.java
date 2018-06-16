package com.milbar.logic.encryption.wrappers.data;

import com.milbar.logic.encryption.factories.RSACipherFactory;

import java.io.Serializable;

public class RSAEncryptedObject<E extends Serializable> extends EncryptedObject<E> implements Serializable {
    
    public RSAEncryptedObject(byte[] serializedObject, RSACipherFactory cipherFactory) {
        super(serializedObject, cipherFactory, true);
    }
    
    @Override
    public RSACipherFactory getCipherFactory() {
        return (RSACipherFactory) cipherFactory;
    }
}

