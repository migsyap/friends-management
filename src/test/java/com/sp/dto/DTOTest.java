package com.sp.dto;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.Objects;

import static java.util.Collections.singletonList;

public class DTOTest {
    @Test
    public void connectionRequest__non_null_friends() {
        ConnectionRequest request = ConnectionRequest.builder().friends(null).build();
        assert (Objects.nonNull(request.getFriends()));
    }

    @Test
    public void connectionRequest__invalid_request() {
        ConnectionRequest request = ConnectionRequest.builder().friends(singletonList("mike@mail.com")).build();
        assert (!request.isValidRequest());
        assert (StringUtils.isBlank(request.getEmail1()));
        assert (StringUtils.isBlank(request.getEmail2()));
    }

    @Test
    public void connectionRequest__valid_request() {
        ConnectionRequest request = ConnectionRequest.builder().friends(Arrays.asList("mike@mail.com", "george@mail.com")).build();
        assert (request.isValidRequest());
        assert ("mike@mail.com".equals(request.getEmail1()));
        assert ("george@mail.com".equals(request.getEmail2()));
    }
}
