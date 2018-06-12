package com.milbar.logic.abstracts;

public enum BlockModes {
    
    ECB("ElectronicCodebook", "ECB", false),
    CBC("CipherBlockChaining", "CBC", true),
    CFB("CipherFeedbackMode", "CFB", true),
    OFB("OutputFeedbackMode", "OFB", true);
    
    public String fullName;
    public String shortName;
    public boolean initVectorRequired;
    
    BlockModes(String fullName, String shortName, boolean initVectorRequired) {
        this.fullName = fullName;
        this.shortName = shortName;
        this.initVectorRequired = initVectorRequired;
    }
    
}
