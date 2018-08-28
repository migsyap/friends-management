package com.sp.component;

import com.sp.dto.ConnectionRequest;
import com.sp.dto.FindConnectionsRequest;
import com.sp.dto.Response;
import com.sp.dto.SubscriptionRequest;
import com.sp.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class RestApisTest {
    @InjectMocks
    private RestApis controller;

    @Mock
    private ManagementService manager;

    @Before
    public void init() {
        manager = mock(ManagementService.class);
        controller = new RestApis(manager);
    }

    @Test
    public void connect__business_exception() throws BusinessException {
        ResponseEntity resp;
        Response r;

        Mockito.doThrow(new BusinessException("test exception")).when(manager).createConnection(any(ConnectionRequest.class));
        resp = controller.connect(ConnectionRequest.builder().build());
        assert (HttpStatus.NOT_ACCEPTABLE.equals(resp.getStatusCode()));
        assert (resp.getBody() instanceof Response);

        r = (Response) resp.getBody();
        assert (Objects.nonNull(r));
        assert (!r.isSuccess());
        assert ("test exception".equals(r.getMessage()));
    }

    @Test
    public void connect__success() throws BusinessException {
        ResponseEntity resp;
        Response r;

        doNothing().when(manager).createConnection(any(ConnectionRequest.class));
        resp = controller.connect(ConnectionRequest.builder().build());
        assert (HttpStatus.OK.equals(resp.getStatusCode()));
        assert (resp.getBody() instanceof Response);

        r = (Response) resp.getBody();
        assert (Objects.nonNull(r));
        assert (r.isSuccess());
        assert (StringUtils.isBlank(r.getMessage()));
    }

    @Test
    public void findConnections() {
        FindConnectionsRequest request = FindConnectionsRequest.builder().email("mike@mail.com").build();
        Response r;

        when(manager.findConnections(eq(request))).thenReturn(Collections.emptyList());
        ResponseEntity resp = controller.findConnections(request);
        assert (HttpStatus.OK.equals(resp.getStatusCode()));
        assert (resp.getBody() instanceof Response);

        r = (Response) resp.getBody();
        assert (r.isSuccess());
        assert (r.getFriends().isEmpty());
        assert (r.getCount() == 0);

        when(manager.findConnections(eq(request))).thenReturn(Collections.singletonList("george@mail.com"));
        resp = controller.findConnections(request);
        assert (HttpStatus.OK.equals(resp.getStatusCode()));
        assert (resp.getBody() instanceof Response);

        r = (Response) resp.getBody();
        assert (r.isSuccess());
        assert (!r.getFriends().isEmpty());
        assert (r.getCount() == 1);
    }

    @Test
    public void findCommonConnections__business_exception() throws BusinessException {
        ResponseEntity resp;
        Response r;

        Mockito.doThrow(new BusinessException("test exception")).when(manager).findCommonConnections(any(ConnectionRequest.class));
        resp = controller.findCommonConnections(ConnectionRequest.builder().build());
        assert (HttpStatus.NOT_ACCEPTABLE.equals(resp.getStatusCode()));
        assert (resp.getBody() instanceof Response);

        r = (Response) resp.getBody();
        assert (Objects.nonNull(r));
        assert (!r.isSuccess());
        assert ("test exception".equals(r.getMessage()));
    }

    @Test
    public void findCommonConnections__success() throws BusinessException {
        ConnectionRequest request = ConnectionRequest.builder().friends(Arrays.asList("mike@mail.com", "george@mail.com")).build();
        Response r;

        when(manager.findCommonConnections(eq(request))).thenReturn(Collections.emptyList());
        ResponseEntity resp = controller.findCommonConnections(request);
        assert (HttpStatus.OK.equals(resp.getStatusCode()));
        assert (resp.getBody() instanceof Response);

        r = (Response) resp.getBody();
        assert (r.isSuccess());
        assert (r.getFriends().isEmpty());
        assert (r.getCount() == 0);

        when(manager.findCommonConnections(eq(request))).thenReturn(Collections.singletonList("matt@mail.com"));
        resp = controller.findCommonConnections(request);
        assert (HttpStatus.OK.equals(resp.getStatusCode()));
        assert (resp.getBody() instanceof Response);

        r = (Response) resp.getBody();
        assert (r.isSuccess());
        assert (!r.getFriends().isEmpty());
        assert (r.getCount() == 1);
    }

    @Test
    public void subscribe__business_exception() throws BusinessException {
        ResponseEntity resp;
        Response r;

        Mockito.doThrow(new BusinessException("test exception")).when(manager).subscribe(any(SubscriptionRequest.class));
        resp = controller.subscribe(SubscriptionRequest.builder().build());
        assert (HttpStatus.NOT_ACCEPTABLE.equals(resp.getStatusCode()));
        assert (resp.getBody() instanceof Response);

        r = (Response) resp.getBody();
        assert (Objects.nonNull(r));
        assert (!r.isSuccess());
        assert ("test exception".equals(r.getMessage()));
    }

    @Test
    public void subscribe__success() throws BusinessException {
        SubscriptionRequest request = SubscriptionRequest.builder().requestor("mike@mail.com").target("george@mail.com").build();
        Response r;

        doNothing().when(manager).subscribe(eq(request));
        ResponseEntity resp = controller.subscribe(request);
        assert (HttpStatus.OK.equals(resp.getStatusCode()));
        assert (resp.getBody() instanceof Response);

        r = (Response) resp.getBody();
        assert (r.isSuccess());
        assert (StringUtils.isBlank(r.getMessage()));
    }

    @Test
    public void block__business_exception() throws BusinessException {
        ResponseEntity resp;
        Response r;

        Mockito.doThrow(new BusinessException("test exception")).when(manager).block(any(SubscriptionRequest.class));
        resp = controller.block(SubscriptionRequest.builder().build());
        assert (HttpStatus.NOT_ACCEPTABLE.equals(resp.getStatusCode()));
        assert (resp.getBody() instanceof Response);

        r = (Response) resp.getBody();
        assert (Objects.nonNull(r));
        assert (!r.isSuccess());
        assert ("test exception".equals(r.getMessage()));
    }

    @Test
    public void block__success() throws BusinessException {
        SubscriptionRequest request = SubscriptionRequest.builder().requestor("mike@mail.com").target("george@mail.com").build();
        Response r;

        doNothing().when(manager).block(eq(request));
        ResponseEntity resp = controller.block(request);
        assert (HttpStatus.OK.equals(resp.getStatusCode()));
        assert (resp.getBody() instanceof Response);

        r = (Response) resp.getBody();
        assert (r.isSuccess());
        assert (StringUtils.isBlank(r.getMessage()));
    }
}
