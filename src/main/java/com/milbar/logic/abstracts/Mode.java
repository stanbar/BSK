package com.milbar.logic.abstracts;

import java.util.function.Supplier;

public enum Mode implements Supplier<String> {
    ECB("ECB", false),
    CBC("CBC", true),
    CFB("CFB", true),
    OFB("OFB", true);
    
    public String fullName;
    public boolean initVectorRequired;
    
    Mode(String fullName, boolean initVectorRequired) {
        this.fullName = fullName;
        this.initVectorRequired = initVectorRequired;
    }
    
    
    @Override
    public String get() {
        return fullName;
    }
}
