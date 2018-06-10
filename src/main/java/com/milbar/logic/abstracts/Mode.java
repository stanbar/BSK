package com.milbar.logic.abstracts;

/**
 * https://docs.oracle.com/javase/6/docs/technotes/guides/security/SunProviders.html
 */

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
