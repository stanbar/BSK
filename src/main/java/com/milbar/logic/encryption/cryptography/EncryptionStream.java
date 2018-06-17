package com.milbar.logic.encryption.cryptography;

import com.milbar.logic.abstracts.Mode;
import com.milbar.logic.encryption.wrappers.data.AESKeyEncrypted;
import com.milbar.logic.exceptions.EncryptionException;
import com.milbar.logic.security.wrappers.Password;

import java.security.PublicKey;
import java.util.Map;

public interface EncryptionStream {
    
    /**
     * This method is using AES algorithm to encrypt given input stream and write it to given output stream.
     * Output stream contain serialized header with AESCipherFactory, that can be used for decryption.
     * @param password Password that will be used with PBEKeySpec for AES encryption.
     * @param blockModeType Data block mode type, that will be used in AES algorithm.
     * @throws EncryptionException If there is any error with encryption, then it's caught and thrown as
     *                             EncryptionException, with original or custom error message.
     */
    void encryptStream(Password password, Mode blockModeType) throws EncryptionException;
    
    /**
     * This method is using AES algorithm to encrypt given input stream and write it to given output stream.
     * Output stream contains serialized header with RSACipherFactory, that can be used for decryption.
     * @param publicKey Public key used for encryption.
     */
    void encryptStream(PublicKey publicKey) throws EncryptionException;
    
    /**
     *
     * @param fileExtension
     * @param password
     * @param blockModeType
     * @throws EncryptionException
     */
    void encryptStream(String fileExtension, Password password, Map<String ,AESKeyEncrypted> approvedUsers, Mode blockModeType) throws EncryptionException;
    
}
