package com.milbar.logic.encryption.cryptography;

import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CipherHeaderManager {
    
    // in bytes
    private final static int HEADER_METADATA_SIZE = 5;
    private final static int INT_SIZE = 4;
    
    public static <E extends Serializable> void writeCipherData(E cipherData, OutputStream outputStream) throws IOException {
        byte[] cipherDataSerialized = SerializationUtils.serialize(cipherData);
        byte[] serializedDataLength = convertIntToByteArray(cipherDataSerialized.length);
        
        outputStream.write(getEndianByte());
        outputStream.write(serializedDataLength);
        outputStream.write(cipherDataSerialized);
    }
    
    public static <E extends Serializable> E readCipherData(InputStream inputStream) throws IOException {
        int cipherDataLength = getHeaderSize(inputStream);
        
        byte[] cipherDataBytes = new byte[cipherDataLength];
        inputStream.readNBytes(cipherDataBytes, 0, cipherDataLength);
        
        return (E)SerializationUtils.deserialize(cipherDataBytes);
    }
    
    public static int getTotalHeaderSize(InputStream inputStream) throws IOException {
        byte isBigEndian = (byte) inputStream.read();
        byte[] bytesWithCipherDataLength = new byte[INT_SIZE];
        inputStream.readNBytes(bytesWithCipherDataLength, 0, INT_SIZE);
        int cipherDataLength = getCipherDataLength(bytesWithCipherDataLength, isBigEndian);
        return cipherDataLength + HEADER_METADATA_SIZE;
    }
    
    private static int getHeaderSize(InputStream inputStream) throws IOException {
        return getTotalHeaderSize(inputStream) - HEADER_METADATA_SIZE;
    }
    
    private static int getCipherDataLength(byte[] bytesWithCipherDataLength, byte isBigEndian) {
        return isBigEndian == 1 ? convertByteArrayToInt(bytesWithCipherDataLength, true)
                : convertByteArrayToInt(bytesWithCipherDataLength, false);
    }
    
    private static byte[] convertIntToByteArray(int integer) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(INT_SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        byteBuffer.put(ByteBuffer.allocate(INT_SIZE).order(ByteOrder.nativeOrder()).putInt(integer).array());
        return byteBuffer.array();
    }
    
    private static int convertByteArrayToInt(byte[] byteArray, boolean isBigEndian) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
        if (isBigEndian)
            byteBuffer.order(ByteOrder.BIG_ENDIAN);
        else
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        
        return byteBuffer.getInt();
    }
    
    private static byte getEndianByte() {
        if (isBigEndian())
            return 1;
        else
            return 0;
    }
    
    private static boolean isBigEndian() {
        return ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);
    }
    
}
