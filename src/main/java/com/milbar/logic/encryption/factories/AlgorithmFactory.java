package com.milbar.logic.encryption.factories;

import java.io.Serializable;

public abstract class AlgorithmFactory implements Serializable {
    
    abstract String getAlgorithmName();
    
    abstract int getKeySize();
    
}
