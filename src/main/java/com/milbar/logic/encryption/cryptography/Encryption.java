package com.milbar.logic.encryption.cryptography;

import com.milbar.logic.abstracts.EncryptionBlockModeType;
import com.milbar.logic.encryption.wrappers.data.AESEncryptedObject;
import com.milbar.logic.encryption.wrappers.data.RSAEncryptedObject;
import com.milbar.logic.exceptions.EncryptionException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.PublicKey;

public interface Encryption {
    
    /**
     * This method is using AES algorithm to encrypt given object and wrap it with AESEncryptedObject class,
     * which represents encrypted, serialized object data and it's AES parameters needed for decryption.
     * @param object Any object implementing Serializable interface.
     * @param password Password that will be used with PBEKeySpec for AES encryption.
     * @param blockModeType Data block mode type, that will be used in AES algorithm.
     * @param <E> The type of an object, that is going to be encrypted.
     * @return Wrapped in AESEncryptedObject serialized bytes of original object with AES parameters, needed
     *         needed for decryption.
     * @throws EncryptionException If there is any error with encryption, then it's caught and thrown as
     *                             EncryptionException, with original or custom error message.
     */
    <E extends Serializable> AESEncryptedObject<E>
    encryptObject(E object, char[] password, EncryptionBlockModeType blockModeType) throws EncryptionException;
    
    /**
     * This method is using RSA algorithm to encrypt given object and wrap it with RSAEncryptedObject class,
     * which is representing encrypted, serialized object data and it's RSA parameters needed for decryption.
     * @param object Any object implementing Serializable interface.
     * @param publicKey Public key used for encryption.
     * @param <E> The type of an object, that is going to be encrypted.
     * @return Wrapped in RSAEncryptedObject serialized bytes of original object with RSA parameters, needed
     *         needed for decryption. It's completely safe to save it without any more encryption, because
     *         it doesn't contain any confidential data.
     * @throws EncryptionException If there is any error with encryption, then it's caught and thrown as
     *                             EncryptionException, with original or custom error message.
     */
    <E extends Serializable> RSAEncryptedObject<E>
    encryptObject(E object, PublicKey publicKey) throws EncryptionException;
    
    /**
     * This method is using AES algorithm to encrypt given input stream and write it to given output stream.
     * Output stream contain serialized header with AESCipherFactory, that can be used for decryption.
     * @param inputStream Input stream with data to encrypt.
     * @param outputStream Output stream to which the data will be saved after encryption.
     * @param password Password that will be used with PBEKeySpec for AES encryption.
     * @param blockModeType Data block mode type, that will be used in AES algorithm.
     * @throws EncryptionException If there is any error with encryption, then it's caught and thrown as
     *                             EncryptionException, with original or custom error message.
     */
    void encryptStream(InputStream inputStream, OutputStream outputStream,
                       char[] password, EncryptionBlockModeType blockModeType) throws EncryptionException;
    
    /**
     * This method is using AES algorithm to encrypt given input stream and write it to given output stream.
     * Output stream contains serialized header with RSACipherFactory, that can be used for decryption.
     * @param inputStream Input stream with data to encrypt.
     * @param outputStream Output stream to which the data will be saved after encryption.
     * @param publicKey Public key used for encryption.
     */
    void encryptStream(InputStream inputStream, OutputStream outputStream, PublicKey publicKey) throws EncryptionException;
    
}
