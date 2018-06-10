package com.milbar.logic.encryption.wrappers;

import javax.crypto.spec.IvParameterSpec;
import java.io.Serializable;

public class EncryptedData implements Serializable {
    
    private byte[] data;
    private IvParameterSpec ivParameterSpec;
    
    public EncryptedData(IvParameterSpec ivParameterSpec, byte[] data) {
        this.ivParameterSpec = ivParameterSpec;
        this.data = data;
    }
    
    
    public byte[] getData() {
        return data;
    }
    
    public IvParameterSpec getIvParameterSpec() {
        return ivParameterSpec;
    }
}
