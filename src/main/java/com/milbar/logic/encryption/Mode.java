package com.milbar.logic.encryption;

public enum Mode {
    ECB("ElectronicCodebook", false),
    CBC("CipherBlockChaining", true),
    CFB("CipherFeedbackMode", true),
    OFB("OutputFeedbackMode", true);

    public String fullName;
    public boolean initVectorRequired;

    Mode(String fullName, boolean initVectorRequired) {
        this.fullName = fullName;
        this.initVectorRequired = initVectorRequired;
    }
}
