package com.sp.exception;

public class BlockedException extends BusinessException {
    public BlockedException(String requestor, String target) {
        super(String.format("%s has blocked %s!", requestor, target));
    }
}
