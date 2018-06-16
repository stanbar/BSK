package com.milbar.logic.encryption.cryptography;

import com.milbar.logic.encryption.wrappers.data.AESEncryptedObject;
import com.milbar.logic.encryption.wrappers.data.RSAEncryptedObject;
import com.milbar.logic.exceptions.DecryptionException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.PrivateKey;

public interface Decryption {
    
    /**
     * This method is used for decryption of an object, that is wrapped in AESEncryptedObject class, which represents
     * serialized and encrypted data of an original object, with AES parameters that were used for encryption.
     * @param object Wrapped AES encrypted object, which is going to be decrypted.
     * @param password Password that will be used for creating PBBKeySpec for AES decryption.
     * @param <E> A type of an object, that is stored inside AESEncryptedObject wrapper class.
     * @return Original, decrypted and deserialized object, based on given data from AESEncryptedObject.
     * @throws DecryptionException If there is any error in decryption, then it is caught and thrown as
     *                             DecryptionException, with it's original or custom message.
     */
    <E extends Serializable> E decryptObject(AESEncryptedObject<E> object, char[] password) throws DecryptionException;
    
    /**
     * This method is used for decryption of an object, that is wrapped in RSAEncryptedObject class, which represents
     * serialized and encrypted data of an original object, with RSA parameters that were used for encryption.
     * @param object Wrapped RSA encrypted object, which is going to be decrypted.
     * @param privateKey Private RSA key needed for decryption.
     * @param <E> The type of an object, that is stored inside RSAEncryptedObject wrapper class.
     * @return Original, decrypted and deserialized object, based on given data from RSAEncryptedObject.
     * @throws DecryptionException If there is any error in decryption, then it is caught and thrown as
     *                             DecryptionException, with it's original or custom message.
     */
    <E extends Serializable> E decryptObject(RSAEncryptedObject<E> object, PrivateKey privateKey) throws DecryptionException;
    
    /**
     * This method is used for decryption of encrypted data in given input stream. The encryption algorithm must be AES.
     * The input stream must contain AESFactory serialized header, which was used to encryption.
     * @param inputStream Encrypted stream with data with AES algorithm.
     * @param outputStream Decrypted stream with original data.
     * @param password Password that will be used for creating PBBKeySpec for AES decryption.
     * @throws DecryptionException If there is any error in decryption, then it is caught and thrown as
     *                             DecryptionException, with it's original or custom message.
     */
    void decryptStream(InputStream inputStream, OutputStream outputStream, char[] password) throws DecryptionException;
    
    /**
     * This method is used for decryption of encrypted data in given input stream. The encryption algorithm must be RSA.
     * The input stream must contain RSAFactory serialized header, which was used to encryption.
     * @param inputStream Encrypted stream with data with RSA algorithm.
     * @param outputStream Decrypted stream with original data.
     * @param privateKey Private RSA key needed for decryption.
     * @throws DecryptionException If there is any error in decryption, then it is caught and thrown as
     *                             DecryptionException, with it's original or custom message.
     */
    void decryptStream(InputStream inputStream, OutputStream outputStream, PrivateKey privateKey) throws DecryptionException;
    
}
