package com.sp.exception;

public class ConnectionEstablishedException extends BusinessException {
    public ConnectionEstablishedException(String email1, String email2) {
        super(String.format("Connection between %s and %s is already established!", email1, email2));
    }
}
