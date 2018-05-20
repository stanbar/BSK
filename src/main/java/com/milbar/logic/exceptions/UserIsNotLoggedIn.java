package com.milbar.logic.exceptions;

public class UserIsNotLoggedIn extends Exception {
    public UserIsNotLoggedIn(String msg) {
        super(msg);
    }
}
