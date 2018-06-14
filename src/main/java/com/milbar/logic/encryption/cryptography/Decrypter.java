package com.milbar.logic.encryption.cryptography;

import com.milbar.logic.abstracts.Destroyable;
import com.milbar.logic.exceptions.DecryptionException;

import javax.crypto.Cipher;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Decrypter extends Cryptography implements Destroyable {
    
    public Decrypter(Cipher cipher) {
        super(cipher);
    }
    
    public void decrypt(InputStream inputStream, OutputStream outputStream) throws DecryptionException {
        try {
            streamCryptography(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new DecryptionException(e.getMessage());
        }
    }
    
    public Object decrypt(ObjectInputStream objectInputStream) throws DecryptionException {
        try {
            return singleObjectCryptography(objectInputStream);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DecryptionException(e.getMessage());
        }
    }
    
    public <E extends Serializable> List<E> decrypt(List<E> encryptedObjects) throws DecryptionException {
        try {
            return collectionCryptography(encryptedObjects);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DecryptionException(e.getMessage());
        }
    }
    
    public <E extends Serializable> E decrypt(E encryptedObject) throws DecryptionException {
        List<E> encryptedObjectsCollection = new ArrayList<>();
        encryptedObjectsCollection.add(encryptedObject);
        return decrypt(encryptedObjectsCollection).get(0);
    }
    
    public byte[] decrypt(byte[] encryptedArray) throws DecryptionException {
        try {
            return byteArrayCryptography(encryptedArray);
        } catch (IOException e) {
            e.printStackTrace();
            throw new DecryptionException(e.getMessage());
        }
    }
    
    public char[] decrypt(char[] encryptedArray) throws DecryptionException {
        try {
            return charArrayCryptography(encryptedArray);
        } catch (IOException e) {
            e.printStackTrace();
            throw new DecryptionException(e.getMessage());
        }
    }
}
