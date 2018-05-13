package com.milbar.logic.encryption;

/**
 * https://docs.oracle.com/javase/6/docs/technotes/guides/security/SunProviders.html
 */

public enum Algorithm {
    DES("DES", 56, 8),
    // A keysize of 112 will generate a Triple DES key with 2 intermediate keys,
    // and a keysize of 168 will generate a Triple DES key with 3 intermediate keys.
    DESeee("DES", 56, 8),
    DESede3("DESede", 168, 16),
    DESede2("DESede", 112, 16),
    AES("AES", 128, 16),
    Blowfish("Blowfish", 128, 8);

    public int initVectorSize;
    public int keySize;
    public String algorithmName;

    Algorithm(String algorithmName, int keySize, int initVectorSize) {
        this.algorithmName = algorithmName;
        this.keySize = keySize;
        this.initVectorSize = initVectorSize;
    }

}
