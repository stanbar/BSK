package com.milbar.logic.encryption.factories;

public class RSAFactory extends AlgorithmFactory {
    
    
    private final static String ALGORITHM_NAME = "RSA";
    private final static String ALGORITHM_FULL_NAME = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    
    private final static int KEY_SIZE = 2048;
    
    @Override
    String getAlgorithmName() {
        return ALGORITHM_NAME;
    }
    
    String getFullAlgorithmName() {
        return ALGORITHM_FULL_NAME;
    }
    
    @Override
    int getKeySize() {
        return KEY_SIZE;
    }
}
