package com.sp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateRequest {
    private String sender;
    private String text;

    public boolean isValidRequest() {
        return StringUtils.isNotBlank(sender) && StringUtils.isNotBlank(text);
    }

    public String getSender() {
        return isValidRequest() ? StringUtils.defaultString(sender).trim() : StringUtils.EMPTY;
    }

    public String getText() {
        return isValidRequest() ? StringUtils.defaultString(text).trim() : StringUtils.EMPTY;
    }

    public List<String> getMentions() {
        Pattern simpleEmailRegex = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        return Arrays.stream(getText().split(" "))
                .filter(s -> simpleEmailRegex.matcher(s).find()).collect(Collectors.toList());
    }
}
