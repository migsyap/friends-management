package com.sp.exception;

public class InvalidSubscriptionException extends BusinessException {
    public InvalidSubscriptionException() {
        super("Both the requestor and the target must be provided.");
    }
}
