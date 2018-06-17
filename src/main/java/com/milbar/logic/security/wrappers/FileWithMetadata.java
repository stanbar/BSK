package com.milbar.logic.security.wrappers;

import com.milbar.logic.abstracts.Destroyable;
import com.milbar.logic.abstracts.Mode;
import com.milbar.logic.encryption.factories.AESCipherFactory;
import com.milbar.logic.encryption.factories.AESFactory;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileWithMetadata implements Destroyable {
    
    private File fileInput;
    private File fileOutput;
    
    private Password password;
    private Mode mode;
    private boolean encryption;
    private String fileExtension;
    
    private FileWithMetadata() {}
    
    private FileWithMetadata(File fileInput, File fileOutput, String fileExtension, Password password, Mode mode, boolean encryption) {
        this.fileInput = fileInput;
        this.fileOutput = fileOutput;
        this.fileExtension = FilenameUtils.getExtension(fileInput.getName());
        this.encryption = encryption;
        this.password = password;
        this.mode = mode;
        this.fileExtension = fileExtension;
        this.fileOutput.getParentFile().mkdirs();
    }
    
    public static FileWithMetadata getEncryptionInstance(File fileInput, Path destination, Password password, Mode mode) {
        String fileNameWithoutExt = getFilenameWithoutExt(fileInput);
        File fileOutput = getFileOutput(destination, fileNameWithoutExt);
        String fileExtension = getFileExtension(fileInput);
        
        return new FileWithMetadata(fileInput, fileOutput, fileExtension, password, mode, true);
    }
    
    public static FileWithMetadata getDecryptionInstance(File fileInput, Path destination, Password password, Mode mode) throws IOException {
        String fileNameWithoutExt = getFilenameWithoutExt(fileInput);
        try (FileInputStream fileInputStream = new FileInputStream(fileInput)) {
            AESCipherFactory aesCipherFactory = new AESCipherFactory(new AESFactory(), mode);
            String fileExtension = aesCipherFactory.getOriginalFileExtension();
    
            File fileOutput = Paths.get(destination.normalize().toString(), fileNameWithoutExt + fileExtension).toFile();
            return new FileWithMetadata(fileInput, fileOutput, fileExtension, password, mode, false);
        }
    }
    
    private static String getFilenameWithoutExt(File file) {
        return FilenameUtils.removeExtension(file.getName());
    }
    
    private static File getFileOutput(Path destination, String fileNameWithoutExt) {
        File fileOutput = Paths.get(destination.normalize().toString(), fileNameWithoutExt + ".enc").toFile();
        
        long counter = 1;
        while (fileOutput.exists() && !fileOutput.isDirectory()) {
            fileOutput = getNextFileName(destination, fileNameWithoutExt, counter);
            counter++;
        }
        return fileOutput;
    }
    
    private static File getNextFileName(Path destination, String fileNameWithoutExt, long number) {
        return Paths.get(destination.normalize().toString(), fileNameWithoutExt + number + ".enc").toFile();
    }
    
    private static String getFileExtension(File file) {
        return FilenameUtils.getExtension(file.getName());
    }
    
    public File getFileInput() {
        return fileInput;
    }
    
    public File getFileOutput() {
        return fileOutput;
    }
    
    public Password getPassword() {
        return password;
    }
    
    public Mode getMode() {
        return mode;
    }
    
    public boolean isEncryption() {
        return encryption;
    }
    
    public String getFileExtension() {
        return fileExtension;
    }
    
    @Override
    public void destroy() {
       password.destroy();
    }
}
