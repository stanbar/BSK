package com.milbar.logic.exceptions;

public class UserAlreadyExists extends Exception {
    public UserAlreadyExists(String msg) {
        super(msg);
    }
}
