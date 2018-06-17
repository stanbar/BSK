package com.milbar.logic.security.jobs;

import com.milbar.logic.abstracts.Destroyable;
import com.milbar.logic.encryption.cryptography.DecryptionStream;
import com.milbar.logic.encryption.cryptography.EncryptionStream;
import com.milbar.logic.encryption.cryptography.StreamCryptography;
import com.milbar.logic.exceptions.DecryptionException;
import com.milbar.logic.exceptions.EncryptionException;
import com.milbar.logic.security.wrappers.FileWithMetadata;
import com.milbar.logic.security.wrappers.ProgressListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AESFileCipherJob extends Task implements Destroyable, ProgressListener {
    
    private final SimpleStringProperty fileName = new SimpleStringProperty();
    private final SimpleStringProperty status = new SimpleStringProperty();
    private final DoubleProperty progress = new SimpleDoubleProperty();
    
    private FileWithMetadata fileWithMetadata;
    
    private long totalStreamSize = 0;
    private long streamReadProgress = 0;

    private boolean isFailed = false;
    private boolean finished = false;
    
    private AESFileCipherJob(long size, long progress, String msg) {
        this.totalStreamSize = size;
        this.streamReadProgress = progress;
        onProgressChanged(progress);
        status.set(msg);
        isFailed = true;
        finished = true;
    }
    
    public AESFileCipherJob(FileWithMetadata fileWithMetadata) {
        this.fileWithMetadata = fileWithMetadata;
    }
    
    public static AESFileCipherJob getFailedInstance() {
        return new AESFileCipherJob(1, 1, "Failed");
    }
    
    @Override
    public Object call() {
        if (isFailed)
            return null;
        
        if (fileWithMetadata.isEncryption())
            encryptFile();
        else
            decryptFile();
    
        finished = true;
        return null;
    }
    
    private void encryptFile() {
        try (FileInputStream fileInputStream = new FileInputStream(fileWithMetadata.getFileInput());
             FileOutputStream fileOutputStream = new FileOutputStream(fileWithMetadata.getFileOutput())) {
            
            EncryptionStream encryptionStream = new StreamCryptography(this, fileInputStream, fileOutputStream);
            encryptionStream.encryptStream(fileWithMetadata.getPassword(), fileWithMetadata.getMode());
            
            finished("Finished encryption");
            
        } catch (IOException | EncryptionException e) {
            reset("Encryption failed");
        }
    }
    
    private void decryptFile() {
        try (FileInputStream fileInputStream = new FileInputStream(fileWithMetadata.getFileInput());
             FileOutputStream fileOutputStream = new FileOutputStream(fileWithMetadata.getFileOutput())) {
    
            DecryptionStream encryptionStream  = new StreamCryptography(this, fileInputStream, fileOutputStream);
            encryptionStream.decryptStream(fileWithMetadata.getPassword());
    
            finished("Finished decryption");
    
        } catch (IOException | DecryptionException e) {
            reset("Decryption failed.");
        }
    }

    public void reset() {
        reset("Waiting..");
    }
    
    private void reset(String msg) {
        streamReadProgress = 0;
        totalStreamSize = 0;
        status.set(msg);
        progress.set(0.0);
    }
    
    private void finished(String msg) {
        status.set(msg);
        progress.set(100.0);
    }
    
    public File getFile() {
        return fileWithMetadata.getFileInput();
    }

    public SimpleStringProperty getStatusProperty() {
        return status;
    }

    public DoubleProperty getProgressProperty() {
        return progress;
    }
    
    @Override
    public void destroy() {
        fileWithMetadata.destroy();
    }
    
    @Override
    public void initializeProgress(long total) {
        this.totalStreamSize = total;
    }
    
    @Override
    public void onProgressChanged(long change) {
        long delta = change - streamReadProgress;
        streamReadProgress += delta;
        progress.set(streamReadProgress / totalStreamSize);
    }
    
    public boolean isFinished() {
        return finished;
    }
}
