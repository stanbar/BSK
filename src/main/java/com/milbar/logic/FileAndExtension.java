package com.milbar.logic;

import com.milbar.logic.exceptions.IllegalFileExtensionException;
import com.milbar.logic.exceptions.IllegalFileNameException;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileAndExtension {

    private final static String NO_FILE_EXTENSION = "NO_EXTENSION";

    private File file;
    private String fileName, fileExtension;

    public FileAndExtension(File file) {
        this.file = file;
    }

    public void readFileNameAndExtension() throws IllegalFileNameException {
        String fileName = file.getName();

        if (fileName.contains("/"))
            throw new IllegalFileNameException(fileName);

        if (fileName.contains(".")) {

            String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
            String name = fileName.substring(0, fileName.lastIndexOf("."));

            if (name.length() == 0 || extension.length() == 0)
                throw new IllegalFileNameException(fileName);

            if (extension.toUpperCase().equals(NO_FILE_EXTENSION))
                throw new IllegalFileExtensionException(extension);

            this.fileExtension = extension;
            this.fileName = name;
        } else {
            this.fileExtension = NO_FILE_EXTENSION;
            this.fileName = fileName;
        }
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}