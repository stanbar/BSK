package com.milbar.logic.encryption.cryptography;

import com.milbar.logic.abstracts.Mode;
import com.milbar.logic.encryption.factories.AESCipherFactory;
import com.milbar.logic.encryption.factories.AESFactory;
import com.milbar.logic.encryption.factories.RSACipherFactory;
import com.milbar.logic.encryption.factories.RSAFactory;
import com.milbar.logic.exceptions.DecryptionException;
import com.milbar.logic.exceptions.EncryptionException;
import com.milbar.logic.security.wrappers.Password;
import com.milbar.logic.security.wrappers.ProgressInputStream;
import com.milbar.logic.security.wrappers.ProgressListener;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class StreamCryptography implements EncryptionStream, DecryptionStream {
    
    private InputStream inputStream;
    private OutputStream outputStream;
    private ProgressListener jobToUpdateProgress;
    
    public StreamCryptography(ProgressListener jobToUpdateProgress, InputStream inputStream, OutputStream outputStream) {
        this.jobToUpdateProgress = jobToUpdateProgress;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }
    
    @Override
    public void encryptStream(Password password, Mode blockModeType) throws EncryptionException {
        AESFactory aesAlgorithmData = new AESFactory();
        AESCipherFactory aesCipherFactory = new AESCipherFactory(aesAlgorithmData, blockModeType);
        encryptAESStream(password, aesCipherFactory);
    }
    
    @Override
    public void encryptStream(PublicKey publicKey) throws EncryptionException {
        RSACipherFactory cipherFactory = new RSACipherFactory(new RSAFactory());
        try {
            Cipher cipher = cipherFactory.getEncryptCipher(publicKey);
            
            encryptStream(cipher);
            
        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new EncryptionException(e);
        }
    }
    
    @Override
    public void encryptStream(String fileExtension, Password password, Mode blockModeType) throws EncryptionException {
        AESFactory aesAlgorithmData = new AESFactory();
        AESCipherFactory aesCipherFactory =
                new AESCipherFactory(aesAlgorithmData, blockModeType, fileExtension);
        
        encryptAESStream(password, aesCipherFactory);
    }
    
    private void encryptAESStream(Password password, AESCipherFactory aesCipherFactory) throws EncryptionException {
        try {
            Cipher cipher = aesCipherFactory.getCipher(password.getSecret(), Cipher.ENCRYPT_MODE);
            CipherHeaderManager.writeCipherData(aesCipherFactory, outputStream);
            
            encryptStream(cipher);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeySpecException exception) {
            throw new EncryptionException(exception);
        }
    }
    
    private void encryptStream(Cipher cipher) throws IOException {
        try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
             ProgressInputStream progressInputStream = new ProgressInputStream(inputStream, jobToUpdateProgress, inputStream.available())) {
            
            progressInputStream.transferTo(cipherOutputStream);
        }
    }
    
    @Override
    public String decryptStream(Password password) throws DecryptionException {
        try {
            return decryptAESStream(password);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeySpecException exception) {
            throw new DecryptionException(exception);
        }
    }
    
    private String decryptAESStream(Password password) throws InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IOException {
        AESCipherFactory aesCipherFactory = CipherHeaderManager.readCipherData(inputStream);
        String originalFileExtension = aesCipherFactory.getOriginalFileExtension();
        Cipher cipher = aesCipherFactory.getCipher(password.getSecret(), Cipher.DECRYPT_MODE);
        decryptStream(cipher);
        return originalFileExtension;
    }
    
    @Override
    public void decryptStream(PrivateKey privateKey) throws DecryptionException {
        try {
            RSACipherFactory rsaCipherFactory = CipherHeaderManager.readCipherData(inputStream);
            Cipher cipher = rsaCipherFactory.getDecryptCipher(privateKey);
    
            decryptStream(cipher);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException exception) {
            throw new DecryptionException(exception);
        }
    }
    
    private void decryptStream(Cipher cipher) throws IOException {
        try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
             ProgressInputStream progressInputStream = new ProgressInputStream(inputStream, jobToUpdateProgress, inputStream.available())) {
            
            inputStream.transferTo(cipherOutputStream);
        }
    }
    
}
