package com.sp.dto;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
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

    @Test
    public void subscriptionRequest__invalid_request() {
        SubscriptionRequest request = SubscriptionRequest.builder().requestor("mike@mail.com").build();
        assert (!request.isValidRequest());
        assert (StringUtils.isBlank(request.getRequestor()));
        assert (StringUtils.isBlank(request.getTarget()));
    }

    @Test
    public void subscriptionRequest__valid_request() {
        SubscriptionRequest request = SubscriptionRequest.builder().requestor("mike@mail.com").target("george@mail.com").build();
        assert (request.isValidRequest());
        assert ("mike@mail.com".equals(request.getRequestor()));
        assert ("george@mail.com".equals(request.getTarget()));
    }

    @Test
    public void updateRequest__invalid_request() {
        UpdateRequest request = UpdateRequest.builder().sender("mike@mail.com").build();
        assert (!request.isValidRequest());
        assert (StringUtils.isBlank(request.getSender()));
        assert (StringUtils.isBlank(request.getText()));
    }

    @Test
    public void updateRequest__valid_request() {
        UpdateRequest request = UpdateRequest.builder().sender("mike@mail.com").text("hello george@mail.com and matt@mail.com").build();
        assert (request.isValidRequest());
        assert ("mike@mail.com".equals(request.getSender()));
        assert ("hello george@mail.com and matt@mail.com".equals(request.getText()));

        List<String> mentions = request.getMentions();
        assert (!mentions.isEmpty());
        assert (mentions.size() == 2);
        assert (mentions.stream().allMatch(Arrays.asList("george@mail.com", "matt@mail.com")::contains));
    }
}
