package com.milbar.logic.abstracts;

public enum EncryptionBlockModeType {
    ECB("ECB", false),
    CBC("CBC", true),
    CFB("CFB", true),
    OFB("OFB", true);
    
    public String fullName;
    public boolean initVectorRequired;
    
    EncryptionBlockModeType(String fullName, boolean initVectorRequired) {
        this.fullName = fullName;
        this.initVectorRequired = initVectorRequired;
    }
    
    
}
