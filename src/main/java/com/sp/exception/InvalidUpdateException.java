package com.sp.exception;

public class InvalidUpdateException extends BusinessException {
    public InvalidUpdateException() {
        super("Both the sender and the text must be provided.");
    }
}
