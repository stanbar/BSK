package com.milbar.logic.security.jobs;

import com.milbar.logic.abstracts.Destroyable;
import com.milbar.logic.abstracts.Mode;
import com.milbar.logic.encryption.cryptography.DecryptionStream;
import com.milbar.logic.encryption.cryptography.EncryptionStream;
import com.milbar.logic.encryption.cryptography.StreamCryptography;
import com.milbar.logic.exceptions.DecryptionException;
import com.milbar.logic.exceptions.EncryptionException;
import com.milbar.logic.security.FileCryptography;
import com.milbar.logic.security.FileForDecryption;
import com.milbar.logic.security.FileForEncryption;
import com.milbar.logic.security.wrappers.Password;
import com.milbar.logic.security.wrappers.ProgressListener;
import javafx.beans.property.*;
import javafx.concurrent.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AESFileCipherJob extends Task implements Destroyable, ProgressListener {
    
    private final SimpleStringProperty status = new SimpleStringProperty();
    private final DoubleProperty progress = new SimpleDoubleProperty();
    private final BooleanProperty isEncryptedCheckbox = new SimpleBooleanProperty(false);
    
    private File file;
    private final FileCryptography fileCryptography;
    private Password sessionKey;
    private Mode blockMode;
    
    private long totalStreamSize = 0;
    private long streamReadProgress = 0;

    public AESFileCipherJob(FileForEncryption fileForEncryption, Password sessionKey, Mode blockMode) {
        this.file = fileForEncryption.getFileInput();
        this.fileCryptography = fileForEncryption;
        this.sessionKey = sessionKey;
        this.blockMode = blockMode;
        reset("Waiting..");
    }
    
    public AESFileCipherJob(FileForDecryption fileForDecryption, Password sessionKey) {
        this.file = fileForDecryption.getFileInput();
        this.sessionKey = sessionKey;
        this.fileCryptography = fileForDecryption;
        reset("Waiting..");
    }

    @Override
    public Object call() {
        
        if (fileCryptography.isEncryption())
            encryptFile();
        else
            decryptFile();
        
        return null;
    }
    
    private void encryptFile() {
        try (FileInputStream fileInputStream = new FileInputStream(fileCryptography.getFileInput());
             FileOutputStream fileOutputStream = new FileOutputStream(fileCryptography.getFileOutput())) {
            
            EncryptionStream encryptionStream = new StreamCryptography(this, fileInputStream, fileOutputStream);
            encryptionStream.encryptStream(sessionKey, blockMode);
            
            finished("Finished encryption");
            
        } catch (IOException | EncryptionException e) {
            reset("Encryption failed");
        }
    }
    
    private void decryptFile() {
        try (FileInputStream fileInputStream = new FileInputStream(fileCryptography.getFileInput());
             FileOutputStream fileOutputStream = new FileOutputStream(fileCryptography.getFileOutput())) {
    
            DecryptionStream encryptionStream  = new StreamCryptography(this, fileInputStream, fileOutputStream);
            encryptionStream.decryptStream(sessionKey);
    
            finished("Finished decryption");
    
        } catch (IOException | DecryptionException e) {
            reset("Decryption failed.");
        }
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
        return file;
    }

    public SimpleStringProperty getStatusProperty() {
        return status;
    }

    public DoubleProperty getProgressProperty() {
        return progress;
    }
    
    @Override
    public void destroy() {
        sessionKey.destroy();
    }
    
    @Override
    public void initializeProgress(long total) {
        this.totalStreamSize = total;
    }
    
    @Override
    public void onProgressChanged(long change) {
        streamReadProgress += change;
        progress.set(streamReadProgress / totalStreamSize);
    }
}
