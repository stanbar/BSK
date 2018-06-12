package com.milbar.logic.encryption.wrappers.data;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.KeyPair;

public class EncryptedRSAFile extends EncryptedRSAStream implements Serializable {
    
    EncryptedRSAFile(InputStream inputStream, OutputStream outputStream, Cipher cipher, KeyPair keyPair) {
        super(inputStream, outputStream, cipher, keyPair);
    }
    
    
    
}
