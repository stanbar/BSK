package com.milbar.logic.exceptions;

public class IllegalFileNameException extends Exception {
    public IllegalFileNameException(String fileName) {
        super(fileName);
    }
}
