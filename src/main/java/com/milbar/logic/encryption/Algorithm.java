package com.milbar.logic.encryption;

public enum Algorithm {
    DES(8, Mode.values()),
    AES(16, Mode.values()),
    Blowfish(8, Mode.values()),
    RSA(0, new Mode[]{Mode.ECB});

    public int initVectorSize;
    public Mode[] supportedModes;

    Algorithm(int initVectorSize, Mode[] supportedModes) {
        this.initVectorSize = initVectorSize;
        this.supportedModes = supportedModes;
    }

}
