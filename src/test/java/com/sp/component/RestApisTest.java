package com.sp.component;

import com.sp.dto.ConnectionRequest;
import com.sp.dto.Response;
import com.sp.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

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
}
