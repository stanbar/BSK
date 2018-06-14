package com.milbar.logic.encryption.cryptography;

import com.milbar.logic.abstracts.Destroyable;
import com.milbar.logic.exceptions.EncryptionException;

import javax.crypto.Cipher;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Encrypter extends Cryptography implements Destroyable {

    public Encrypter(Cipher cipher) {
        super(cipher);
    }
    
    public void encrypt(InputStream inputStream, OutputStream outputStream) throws EncryptionException {
        try {
            streamCryptography(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new EncryptionException(e.getMessage());
        }
    }
    
    public Object encrypt(ObjectInputStream objectInputStream) throws EncryptionException {
        try {
            return singleObjectCryptography(objectInputStream);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new EncryptionException(e.getMessage());
        }
    }
    
    public <E extends Serializable> List<E> encrypt(List<E> objectsForEncryption) throws EncryptionException {
        try {
            return collectionCryptography(objectsForEncryption);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new EncryptionException(e.getMessage());
        }
    }
    
    public <E extends Serializable> E encrypt(E objectForEncryption) throws EncryptionException {
        List<E> listOfObjectsForEncryption = new ArrayList<>(1);
        listOfObjectsForEncryption.add(objectForEncryption);
        return encrypt(listOfObjectsForEncryption).get(0);
    }
    
    public byte[] encrypt(byte[] arrayForEncryption) throws EncryptionException {
        try {
            return byteArrayCryptography(arrayForEncryption);
        } catch (IOException e) {
            e.printStackTrace();
            throw new EncryptionException(e.getMessage());
        }
    }
    
    public char[] encrypt(char[] arrayForEncryption) throws EncryptionException {
        try {
            return charArrayCryptography(arrayForEncryption);
        } catch (IOException e) {
            e.printStackTrace();
            throw new EncryptionException(e.getMessage());
        }
    }
}
