package com.sp.exception;

public class SubscribedException extends BusinessException {
    public SubscribedException(String requestor, String target) {
        super(String.format("%s has already subscribed to %s!", requestor, target));
    }
}
