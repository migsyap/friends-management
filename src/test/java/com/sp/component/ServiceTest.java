package com.sp.component;

import com.sp.db.entity.Connection;
import com.sp.db.entity.Subscription;
import com.sp.db.repo.ConnectionsRepository;
import com.sp.db.repo.SubscriptionsRepository;
import com.sp.dto.ConnectionRequest;
import com.sp.dto.FindConnectionsRequest;
import com.sp.dto.SubscriptionRequest;
import com.sp.dto.UpdateRequest;
import com.sp.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.*;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class ServiceTest {
    @InjectMocks
    private ManagementService manager;

    @Mock
    private ConnectionsRepository connections;

    @Mock
    private SubscriptionsRepository subscriptions;

    @Before
    public void init() {
        connections = mock(ConnectionsRepository.class);
        subscriptions = mock(SubscriptionsRepository.class);
        manager = new ManagementService(connections, subscriptions);
    }

    @Test
    public void createConnection__invalid_request() {
        ConnectionRequest request = ConnectionRequest.builder().build();

        try {
            manager.createConnection(request);
        } catch (BusinessException be) {
            log.info("expected error ::: {}", be.getMessage());
            assert (be instanceof InvalidConnectionException);
        }
    }

    @Test
    public void createConnection__established_connection() {
        Connection testConnection = Connection.builder()
                .id("ID")
                .email1("george@mail.com")
                .email2("mike@mail.com")
                .build();

        ConnectionRequest request = ConnectionRequest.builder().friends(Arrays.asList("mike@mail.com", "george@mail.com")).build();
        when(connections.findConnectionsByEmail1IgnoreCaseAndEmail2IgnoreCase(anyString(), anyString()))
                .thenReturn(Optional.of(Collections.singletonList(testConnection)));

        try {
            manager.createConnection(request);
        } catch (BusinessException be) {
            log.info("expected error ::: {}", be.getMessage());
            assert (be instanceof ConnectionEstablishedException);
        }
    }

    @Test
    public void createConnection__blocked() {
        Subscription fromE1 = Subscription.builder()
                .id("IDS")
                .requestor("mike@mail.com")
                .target("george@mail.com")
                .blocked(true)
                .build();

        ConnectionRequest request = ConnectionRequest.builder().friends(Arrays.asList("mike@mail.com", "george@mail.com")).build();
        when(connections.findConnectionsByEmail1IgnoreCaseAndEmail2IgnoreCase(anyString(), anyString())).thenReturn(Optional.of(Collections.emptyList()));
        when(subscriptions.findSubscriptionsByRequestorIgnoreCaseAndTargetIgnoreCaseAndBlockedIsTrue(eq("mike@mail.com"), eq("george@mail.com")))
                .thenReturn(Optional.of(Collections.singletonList(fromE1)));

        try {
            manager.createConnection(request);
        } catch (BusinessException be) {
            log.info("expected error ::: {}", be.getMessage());
            assert (be instanceof BlockedException);
            assert ("mike@mail.com has blocked george@mail.com!".equals(be.getMessage()));
        }

        Subscription fromE2 = Subscription.builder()
                .id("IDS")
                .requestor("george@mail.com")
                .target("mike@mail.com")
                .blocked(true)
                .build();

        when(connections.findConnectionsByEmail1IgnoreCaseAndEmail2IgnoreCase(anyString(), anyString())).thenReturn(Optional.of(Collections.emptyList()));
        when(subscriptions.findSubscriptionsByRequestorIgnoreCaseAndTargetIgnoreCaseAndBlockedIsTrue(eq("mike@mail.com"), eq("george@mail.com")))
                .thenReturn(Optional.of(Collections.emptyList()));
        when(subscriptions.findSubscriptionsByRequestorIgnoreCaseAndTargetIgnoreCaseAndBlockedIsTrue(eq("george@mail.com"), eq("mike@mail.com")))
                .thenReturn(Optional.of(Collections.singletonList(fromE2)));

        try {
            manager.createConnection(request);
        } catch (BusinessException be) {
            log.info("expected error ::: {}", be.getMessage());
            assert (be instanceof BlockedException);
            assert ("george@mail.com has blocked mike@mail.com!".equals(be.getMessage()));
        }
    }

    @Test
    public void createConnection__success() {
        ConnectionRequest request = ConnectionRequest.builder().friends(Arrays.asList("mike@mail.com", "george@mail.com")).build();
        when(connections.findConnectionsByEmail1IgnoreCaseAndEmail2IgnoreCase(anyString(), anyString())).thenReturn(Optional.of(Collections.emptyList()));
        when(subscriptions.findSubscriptionsByRequestorIgnoreCaseAndTargetIgnoreCaseAndBlockedIsTrue(anyString(), anyString()))
                .thenReturn(Optional.of(Collections.emptyList()));

        try {
            manager.createConnection(request);
        } catch (BusinessException be) {
            log.info("unexpected error ::: {}", be.getMessage());
        }
    }

    @Test
    public void findConnections() {
        when(connections.findConnectionsByEmail1IgnoreCase(anyString())).thenReturn(Collections.singletonList(Connection.builder().email2("mike@mail.com").build()));
        when(connections.findConnectionsByEmail2IgnoreCase(anyString())).thenReturn(Collections.singletonList(Connection.builder().email1("george@mail.com").build()));
        List<String> list = manager.findConnections(FindConnectionsRequest.builder().email("matt@mail.com").build());
        assert (Objects.nonNull(list));
        assert (!list.isEmpty());
        assert (list.size() == 2);
        assert (list.stream().allMatch(Arrays.asList("mike@mail.com", "george@mail.com")::contains));
    }

    @Test
    public void findCommonConnections__invalid_request() {
        ConnectionRequest request = ConnectionRequest.builder().build();

        try {
            manager.findCommonConnections(request);
        } catch (BusinessException be) {
            log.info("expected error ::: {}", be.getMessage());
            assert (be instanceof InvalidConnectionException);
        }
    }

    @Test
    public void findCommonConnections__success() {
        ConnectionRequest request = ConnectionRequest.builder().friends(Arrays.asList("matt@mail.com", "shannon@mail.com")).build();
        when(connections.findConnectionsByEmail1IgnoreCase(anyString())).thenReturn(Collections.singletonList(Connection.builder().email2("mike@mail.com").build()));
        when(connections.findConnectionsByEmail2IgnoreCase(anyString())).thenReturn(Collections.singletonList(Connection.builder().email1("george@mail.com").build()));

        List<String> list;

        try {
            list = manager.findCommonConnections(request);
        } catch (BusinessException be) {
            log.info("unexpected error ::: {}", be.getMessage());
            list = Collections.emptyList();
        }

        assert (Objects.nonNull(list));
        assert (!list.isEmpty());
        assert (list.size() == 2);
        assert (list.stream().allMatch(Arrays.asList("mike@mail.com", "george@mail.com")::contains));
    }

    @Test
    public void subscribe__invalid_request() {
        SubscriptionRequest request = SubscriptionRequest.builder().build();

        try {
            manager.subscribe(request);
        } catch (BusinessException be) {
            log.info("expected error ::: {}", be.getMessage());
            assert (be instanceof InvalidSubscriptionException);
        }
    }

    @Test
    public void subscribe__already_subscribed() {
        SubscriptionRequest request = SubscriptionRequest.builder().requestor("mike@mail.com").target("george@mail.com").build();

        try {
            when(subscriptions.findSubscriptionsByRequestorIgnoreCaseAndTargetIgnoreCase(anyString(), anyString()))
                    .thenReturn(Optional.of(Collections.singletonList(Subscription.builder().id("IDS").requestor("mike@mail.com").target("george@mail.com").build())));
            manager.subscribe(request);
        } catch (BusinessException be) {
            log.info("expected error ::: {}", be.getMessage());
            assert (be instanceof SubscribedException);
        }
    }

    @Test
    public void subscribe__success() {
        SubscriptionRequest request = SubscriptionRequest.builder().requestor("mike@mail.com").target("george@mail.com").build();

        try {
            when(subscriptions.findSubscriptionsByRequestorIgnoreCaseAndTargetIgnoreCase(anyString(), anyString())).thenReturn(Optional.of(Collections.emptyList()));
            manager.subscribe(request);
        } catch (BusinessException be) {
            log.info("unexpected error ::: {}", be.getMessage());
        }
    }

    @Test
    public void block__invalid_request() {
        SubscriptionRequest request = SubscriptionRequest.builder().build();

        try {
            manager.block(request);
        } catch (BusinessException be) {
            log.info("expected error ::: {}", be.getMessage());
            assert (be instanceof InvalidSubscriptionException);
        }
    }

    @Test
    public void block__success() {
        SubscriptionRequest request = SubscriptionRequest.builder().requestor("mike@mail.com").target("george@mail.com").build();

        try {
            when(subscriptions.findSubscriptionsByRequestorIgnoreCaseAndTargetIgnoreCase(anyString(), anyString())).thenReturn(Optional.of(Collections.emptyList()));
            manager.block(request);
        } catch (BusinessException be) {
            log.info("unexpected error ::: {}", be.getMessage());
        }
    }

    @Test
    public void findRecipientsOfUpdate__invalid_request() {
        UpdateRequest request = UpdateRequest.builder().build();

        try {
            manager.findRecipientsOfUpdate(request);
        } catch (BusinessException be) {
            log.info("expected error ::: {}", be.getMessage());
            assert (be instanceof InvalidUpdateException);
        }
    }

    @Test
    public void findRecipientsOfUpdate__success() {
        // Sender of the update is mike

        // Mentions - george & michael
        UpdateRequest request = UpdateRequest.builder().sender("mike@mail.com").text("hello george@mail.com and michael@mail.com").build();

        // Friends - matt & shannon
        when(connections.findConnectionsByEmail1IgnoreCase(anyString())).thenReturn(Collections.singletonList(Connection.builder().email2("matt@mail.com").build()));
        when(connections.findConnectionsByEmail2IgnoreCase(anyString())).thenReturn(Collections.singletonList(Connection.builder().email1("shannon@mail.com").build()));

        // Follower - nigella
        when(subscriptions.findSubscriptionsByTargetIgnoreCaseAndBlockedIsFalse(anyString())).thenReturn(Optional.of(Collections.singletonList(Subscription.builder().requestor("nigella@mail.com").build())));

        // mike has blocked shannon
        when(subscriptions.findSubscriptionsByRequestorIgnoreCaseAndBlockedIsTrue(anyString())).thenReturn(Collections.singletonList(Subscription.builder().target("shannon@mail.com").build()));

        // george has blocked mike
        when(subscriptions.findSubscriptionsByTargetIgnoreCaseAndBlockedIsTrue(anyString())).thenReturn(Collections.singletonList(Subscription.builder().requestor("george@mail.com").build()));

        try {
            List<String> recipients = manager.findRecipientsOfUpdate(request);
            assert (Objects.nonNull(recipients));
            assert (!recipients.isEmpty());
            assert (recipients.stream().allMatch(Arrays.asList("michael@mail.com", "matt@mail.com", "nigella@mail.com")::contains));
        } catch (BusinessException be) {
            log.info("unexpected error ::: {}", be.getMessage());
        }
    }
}
