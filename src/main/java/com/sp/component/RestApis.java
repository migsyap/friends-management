package com.sp.component;

import com.sp.dto.ConnectionRequest;
import com.sp.dto.FindConnectionsRequest;
import com.sp.dto.SubscriptionRequest;
import com.sp.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.sp.constant.ControllerConstants.*;

@RestController
@RequestMapping("/api")
@Slf4j
public class RestApis {
    private ManagementService manager;

    @Autowired
    public RestApis(ManagementService managementService) {
        manager = managementService;
    }

    @PostMapping("/connect")
    ResponseEntity connect(@RequestBody ConnectionRequest request) {
        try {
            manager.createConnection(request);
        } catch (BusinessException be) {
            log.error("Error in creating this connection", be);
            return FAILURE.apply(be.getMessage());
        }

        return SUCCESS; // no error means successful
    }

    @PostMapping("/find/connections")
    ResponseEntity findConnections(@RequestBody FindConnectionsRequest request) {
        return SUCCESS_LIST.apply(manager.findConnections(request));
    }

    @PostMapping("/find/common-connections")
    ResponseEntity findCommonConnections(@RequestBody ConnectionRequest request) {
        try {
            return SUCCESS_LIST.apply(manager.findCommonConnections(request));
        } catch (BusinessException be) {
            log.error("Error in creating this connection", be);
            return FAILURE.apply(be.getMessage());
        }
    }

    @PostMapping("/subscribe")
    ResponseEntity subscribe(@RequestBody SubscriptionRequest request) {
        try {
            manager.subscribe(request);
        } catch (BusinessException be) {
            log.error("Error in creating this subscription", be);
            return FAILURE.apply(be.getMessage());
        }

        return SUCCESS; // no error means successful
    }

    @PostMapping("/block")
    ResponseEntity block(@RequestBody SubscriptionRequest request) {
        try {
            manager.block(request);
        } catch (BusinessException be) {
            log.error("Error in blocking this subscription", be);
            return FAILURE.apply(be.getMessage());
        }

        return SUCCESS; // no error means successful
    }
}
