package com.milbar.logic.encryption.wrappers.data;

import java.io.Serializable;

public class AESKeyEncrypted implements Serializable {
    
    private byte[] keyBytes;
    
    public AESKeyEncrypted(byte[] keyBytes) {
        this.keyBytes = keyBytes;
    }
    
    public byte[] getKeyBytes() {
        return keyBytes;
    }
}
