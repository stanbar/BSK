package com.milbar.logic.security.jobs;

import com.milbar.logic.abstracts.Destroyable;
import com.milbar.logic.encryption.cryptography.DecryptionStream;
import com.milbar.logic.encryption.cryptography.EncryptionStream;
import com.milbar.logic.encryption.cryptography.StreamCryptography;
import com.milbar.logic.encryption.wrappers.data.AESKeyEncrypted;
import com.milbar.logic.exceptions.DecryptionException;
import com.milbar.logic.exceptions.EncryptionException;
import com.milbar.logic.security.wrappers.FileWithMetadata;
import com.milbar.logic.security.wrappers.ProgressListener;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class AESFileCipherJob extends Task<Void> implements Destroyable, ProgressListener {
    
    private final SimpleStringProperty fileName = new SimpleStringProperty();
    private final SimpleStringProperty status = new SimpleStringProperty();
    
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
        updateProgressValue(-1, 1);
    }
    
    public static AESFileCipherJob getFailedInstance() {
        return new AESFileCipherJob(1, 1, "Failed");
    }
    
    @Override
    public Void call() {
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
            Map<String ,AESKeyEncrypted> approvedUsers = fileWithMetadata.getApprovedUsers();
            encryptionStream.encryptStream(fileWithMetadata.getFileExtension(), fileWithMetadata.getPassword(), approvedUsers, fileWithMetadata.getMode());
            
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
        updateProgressValue(0, 1);
    }
    
    private void finished(String msg) {
        status.set(msg);
        updateProgressValue(1, 1);
    }
    
    public File getFile() {
        return fileWithMetadata.getFileInput();
    }

    public SimpleStringProperty getStatusProperty() {
        return status;
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
        updateProgressValue(streamReadProgress, totalStreamSize);
    }
    
    @Override
    public void onStatusChanged(String msg) {
        status.set(msg);
    }
    
    public boolean isFinished() {
        return finished;
    }
    
    private void updateProgressValue(long newValue, long total) {
        Platform.runLater(() -> {
            this.updateProgress(newValue, total);
        });
    }
}
