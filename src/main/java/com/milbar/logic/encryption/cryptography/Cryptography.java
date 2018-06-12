package com.milbar.logic.encryption.cryptography;

import com.milbar.logic.abstracts.Destroyable;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public abstract class Cryptography implements Destroyable {
    
    private Cipher cipher;
    
    Cryptography(Cipher cipher) {
        this.cipher = cipher;
    }
    
    <E extends Serializable> List<E> collectionCryptography(List<E> objects) throws IOException, ClassNotFoundException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objOutputStreamConversion = new ObjectOutputStream(byteArrayOutputStream)) {
            
            objOutputStreamConversion.writeObject(objects.stream());
            
            try (InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                 CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
                 ObjectInputStream objectInputStream = new ObjectInputStream(cipherInputStream)) {
                
                List<E> decryptedObjects = new ArrayList<>(objects.size());
                for (int i = 0; i < objects.size(); i++)
                    decryptedObjects.add((E)objectInputStream.readObject());
                
                return decryptedObjects;
            }
        }
    }
    
    Object singleObjectCryptography(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        try (CipherInputStream cipherInputStream = new CipherInputStream(objectInputStream, cipher);
             ObjectInputStream inputStream = new ObjectInputStream(cipherInputStream)){
        
            return inputStream.readObject();
        
        }
    }
    
    void streamCryptography(InputStream inputStream, OutputStream outputStream) throws IOException {
        try (CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher)) {
            cipherInputStream.transferTo(outputStream);
        }
    }
    
    byte[] byteArrayCryptography(byte[] array) throws IOException {
        try (ByteArrayInputStream byteArrayInputStreamEncrypted = new ByteArrayInputStream(array);
             CipherInputStream cipherInputStream = new CipherInputStream(byteArrayInputStreamEncrypted, cipher);
             ByteArrayInputStream byteArrayInputStreamDecrypted = new ByteArrayInputStream(cipherInputStream.readAllBytes())) {
            
            return byteArrayInputStreamDecrypted.readAllBytes();
        }
    }
    
    char[] charArrayCryptography(char[] array) throws IOException {
        CharBuffer charBuffer = CharBuffer.wrap(array);
        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
        byte[] decryptedArray = byteArrayCryptography(byteBuffer.array());
        byteBuffer = ByteBuffer.wrap(decryptedArray);
        charBuffer = byteBuffer.asCharBuffer();
        return charBuffer.array();
    }
    
    @Override
    public void destroy() {
        cipher = null;
    }
}
