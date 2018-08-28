package com.sp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptionRequest {
    private String requestor;
    private String target;

    public String getRequestor() {
        return isValidRequest() ? StringUtils.defaultString(requestor).trim() : StringUtils.EMPTY;
    }

    public String getTarget() {
        return isValidRequest() ? StringUtils.defaultString(target).trim() : StringUtils.EMPTY;
    }

    public boolean isValidRequest() { // request should contain distinct non-blank requestor and target
        return Stream.of(StringUtils.defaultString(requestor), StringUtils.defaultString(target))
                .map(String::toLowerCase).map(String::trim).filter(StringUtils::isNotBlank)
                .distinct().count() == 2;
    }
}
