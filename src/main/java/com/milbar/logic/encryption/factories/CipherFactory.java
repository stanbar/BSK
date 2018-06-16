package com.milbar.logic.encryption.factories;

import java.io.Serializable;

public abstract class CipherFactory implements Serializable {
    
    abstract String getAlgorithmName();
    
    abstract String getAlgorithmFullName();
    
    abstract int getKeySize();
    
}
