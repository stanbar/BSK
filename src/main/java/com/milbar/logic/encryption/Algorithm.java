package com.milbar.logic.encryption;

public enum Algorithm {
    DES(8),
    AES(16),
    Blowfish(8);

    public int initVectorSize;

    Algorithm(int initVectorSize) {
        this.initVectorSize = initVectorSize;
    }

}
