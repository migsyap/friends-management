package com.sp.exception;

public class InvalidConnectionException extends BusinessException {
    public InvalidConnectionException() {
        super("You may only connect exactly two (2) persons.");
    }
}
