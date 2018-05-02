package com.milbar.logic.exceptions;

public class UnexpectedWindowEventCall extends RuntimeException {
    public UnexpectedWindowEventCall(String msg) {
        super(msg);
    }
}
