package com.milbar.logic;

import com.milbar.Utils;
import com.milbar.logic.abstracts.Algorithm;
import com.milbar.logic.abstracts.Mode;
import com.milbar.logic.exceptions.IllegalFileNameException;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class FileCipherJob extends Task {
    public enum CipherMode {
        ENCRYPT(Cipher.ENCRYPT_MODE), DECRYPT(Cipher.DECRYPT_MODE);
        int cipherMode;

        CipherMode(int cipherMode) {
            this.cipherMode = cipherMode;
        }
    }

    private File file;
    private FileAndExtension fileAndExtension;
    private final SimpleStringProperty status = new SimpleStringProperty();
    private final DoubleProperty progress = new SimpleDoubleProperty();
    private final Algorithm algorithm;
    private final Mode mode;
    private final Key secretKey;
    private final byte[] initVectorBytes;
    private final CipherMode cipherMode;


    public FileCipherJob(File file, CipherMode cipherMode, Algorithm algorithm, Mode mode, Key secretKey, byte[] initVectorBytes) {
        this.file = file;
        this.algorithm = algorithm;
        this.mode = mode;
        this.secretKey = secretKey;
        this.initVectorBytes = initVectorBytes;
        this.cipherMode = cipherMode;

        progress.set(0.0);
        status.set("Waiting..");
    }

    public void reset() {
        file = null;
        progress.set(0.0);
    }

    @Override
    public Object call() {
        try {
            fileAndExtension = new FileAndExtension(file);
            String name = fileAndExtension.getFileName();
            String extension = fileAndExtension.getFileExtension();

            File outputFile = new File(file.getParent(), String.format("%s_%s.%s",
                    cipherMode == CipherMode.ENCRYPT ? "encrypted" : "decrypted", name, extension));

            FileAndExtension outputFileAndExtension = new FileAndExtension(outputFile);

            status.set(cipherMode == CipherMode.ENCRYPT ? "Encrypting data..." : "Decrypting data...");
            long start = System.currentTimeMillis();
            cipher(fileAndExtension, outputFileAndExtension, cipherMode);
            long total = System.currentTimeMillis() - start;

            status.set(String.format("[%d sec] Done, saved %s data to file: %s",
                    total/1000,
                    cipherMode == CipherMode.ENCRYPT ? "encrypted" : "decrypted",
                    outputFileAndExtension.getFile().getAbsolutePath()));

            setProgress(1.0);
        } catch (IllegalFileNameException | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException e) {
            status.set(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private void cipher(FileAndExtension source, FileAndExtension destination, CipherMode cipherMode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(algorithm.algorithmName + "/" + mode.name() + "/PKCS5Padding");

        IvParameterSpec initVector = new IvParameterSpec(initVectorBytes);

        //TODO security leak, remove this log
        System.out.printf("Using key: %s and InitVector: %s%n",
                Utils.byteArrayToHex(secretKey.getEncoded()),
                Utils.byteArrayToHex(initVectorBytes));

        if (mode == Mode.ECB)
            cipher.init(cipherMode.cipherMode, secretKey);
        else
            cipher.init(cipherMode.cipherMode, secretKey, initVector);

        
        try (InputStream is = new FileInputStream(source.getFile());
             CipherOutputStream os = new CipherOutputStream(new FileOutputStream(destination.getFile()), cipher)) {
            int totalSize = is.available();
            byte[] buffer = new byte[1024 * 8];
            int lastChunkSize;
            int counter = 0;
            while ((lastChunkSize = is.read(buffer)) > 0) {
                os.write(buffer, 0, lastChunkSize);
                counter += lastChunkSize;
                setProgress((double)counter / (double)totalSize);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public File getFile() {
        return file;
    }

    public SimpleStringProperty getStatusProperty() {
        return status;
    }

    public DoubleProperty getProgressProperty() {
        return progress;
    }

    private void setProgress(double progress) {
        this.progress.set(progress);
    }

}
