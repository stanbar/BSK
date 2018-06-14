package com.milbar.logic.encryption.wrappers.data;

import com.milbar.logic.abstracts.CryptoOperations;
import com.milbar.logic.exceptions.DecryptionException;
import com.milbar.logic.exceptions.EncryptionException;

import javax.crypto.Cipher;
import java.io.*;
import java.security.KeyPair;

public class EncryptedRSAFile extends EncryptedRSAStream implements Serializable {
    
    private CryptoOperations lastCryptoOperation = CryptoOperations.NONE;
    
    private File currentInput, currentOutput;
    private File fileForEncryption, encryptedFile;
    
    EncryptedRSAFile(File fileForEncryption, File encryptedFile, Cipher cipher, KeyPair keyPair) throws FileNotFoundException {
        super(new FileInputStream(fileForEncryption), new FileOutputStream(encryptedFile), cipher, keyPair);
        this.fileForEncryption = fileForEncryption;
        this.encryptedFile = encryptedFile;
    }
    
    @Override
    public void encrypt() throws EncryptionException {
        try {
            if (CryptoOperations.swapStreamsNeeded(lastCryptoOperation, CryptoOperations.ENCRYPTION))
                swapStreams();
    
            lastCryptoOperation = CryptoOperations.ENCRYPTION;
            super.encrypt();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new EncryptionException(e.getMessage());
        }
    }
    
    @Override
    public void decrypt() throws DecryptionException {
        try {
            if (CryptoOperations.swapStreamsNeeded(lastCryptoOperation, CryptoOperations.DECRYPTION))
                swapStreams();
    
            lastCryptoOperation = CryptoOperations.DECRYPTION;
            super.decrypt();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new DecryptionException(e.getMessage());
        }
    
    }
    
    private void swapStreams() throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(currentOutput);
        OutputStream outputStream = new FileOutputStream(currentInput);
        
        if (currentInput == fileForEncryption) {
            currentInput = encryptedFile;
            currentOutput = fileForEncryption;
        }
        else {
            currentInput = fileForEncryption;
            currentOutput = encryptedFile;
        }
        super.updateStreams(inputStream, outputStream);
    }
}