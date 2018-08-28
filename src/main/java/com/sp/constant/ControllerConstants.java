package com.sp.constant;

import com.sp.dto.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.function.Function;

public class ControllerConstants {
    public static final ResponseEntity SUCCESS = ResponseEntity.ok(Response.builder().success(true).build());
    public static final Function<List<String>, ResponseEntity> SUCCESS_LIST = l -> ResponseEntity.ok(Response.builder().success(true).friends(l).count(l.size()).build());
    public static final Function<String, ResponseEntity> FAILURE = message -> ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
            .body(Response.builder().success(false).message(message).build());
}
