package com.sp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConnectionRequest {
    private List<String> friends;

    public List<String> getFriends() { // do not return null list of friends
        return Optional.ofNullable(friends).orElse(Collections.emptyList());
    }

    public boolean isValidRequest() { // request should contain exactly 2 distinct non-blank items. Note that we are not validating the email addresses
        return getFriends().stream().map(String::toLowerCase).map(String::trim).filter(StringUtils::isNotBlank)
                .distinct().count() == 2;
    }

    public String getEmail1() {
        return isValidRequest() ? getFriends().get(0).trim() : StringUtils.EMPTY;
    }

    public String getEmail2() {
        return isValidRequest() ? getFriends().get(1).trim() : StringUtils.EMPTY;
    }
}
