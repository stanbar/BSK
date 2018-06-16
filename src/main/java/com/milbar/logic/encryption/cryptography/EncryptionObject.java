package com.milbar.logic.encryption.cryptography;

import com.milbar.logic.abstracts.Mode;
import com.milbar.logic.encryption.wrappers.data.AESEncryptedObject;
import com.milbar.logic.encryption.wrappers.data.RSAEncryptedObject;
import com.milbar.logic.exceptions.EncryptionException;
import com.milbar.logic.security.wrappers.Password;

import java.io.Serializable;
import java.security.PublicKey;

public interface EncryptionObject {
    
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
    encryptObject(E object, Password password, Mode blockModeType) throws EncryptionException;
    
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

}
