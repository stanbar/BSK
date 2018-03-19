package com.milbar.logic;

import com.milbar.logic.exceptions.IllegalFileNameException;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;

import java.io.File;

public class FileEncryptionJob extends Task {

    private File file;
    private FileAndExtension fileAndExtension;
    private SimpleStringProperty status = new SimpleStringProperty();
    private DoubleProperty progress = new SimpleDoubleProperty();

    public FileEncryptionJob(File file) {
        this.file = file;

        progress.set(0.0);
        status.set("Waiting..");
    }

    public void reset() {
        file = null;
        progress.set(0.0);
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

    public void setProgress(double progress) {
        this.progress.set(progress);
    }

    @Override
    public Object call() throws IllegalFileNameException {
        status.set("Encrypting..");
        fileAndExtension = new FileAndExtension(file);
        fileAndExtension.readFileNameAndExtension();

        String name = fileAndExtension.getFileName();
        String extension = fileAndExtension.getFileExtension();

        // todo implement encryption of a file
        return null;
    }
}
