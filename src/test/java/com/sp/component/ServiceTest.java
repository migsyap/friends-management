package com.sp.component;

import com.sp.db.entity.Connection;
import com.sp.db.repo.ConnectionsRepository;
import com.sp.dto.ConnectionRequest;
import com.sp.exception.BusinessException;
import com.sp.exception.ConnectionEstablishedException;
import com.sp.exception.InvalidConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class ServiceTest {
    @InjectMocks
    private FriendsService friends;

    @Mock
    private ConnectionsRepository connection;

    @Before
    public void init() {
        connection = mock(ConnectionsRepository.class);
        friends = new FriendsService(connection);
    }

    @Test
    public void createConnection__invalid_request() {
        ConnectionRequest request = ConnectionRequest.builder().build();

        try {
            friends.createConnection(request);
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
        when(connection.findConnectionsByEmail1IgnoreCaseAndEmail2IgnoreCase(anyString(), anyString())).thenReturn(Optional.of(Collections.singletonList(testConnection)));

        try {
            friends.createConnection(request);
        } catch (BusinessException be) {
            log.info("expected error ::: {}", be.getMessage());
            assert (be instanceof ConnectionEstablishedException);
        }
    }

    @Test
    public void createConnection__success() {
        ConnectionRequest request = ConnectionRequest.builder().friends(Arrays.asList("mike@mail.com", "george@mail.com")).build();
        when(connection.findConnectionsByEmail1IgnoreCaseAndEmail2IgnoreCase(anyString(), anyString())).thenReturn(Optional.of(Collections.emptyList()));

        try {
            friends.createConnection(request);
        } catch (BusinessException be) {
            log.info("unexpected error ::: {}", be.getMessage());
        }
    }
}
