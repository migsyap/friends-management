package com.sp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private boolean success;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> friends;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer count;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> recipients;
}
