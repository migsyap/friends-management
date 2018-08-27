package com.sp.component;

import com.sp.dto.ConnectionRequest;
import com.sp.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.sp.constant.ControllerConstants.FAILURE;
import static com.sp.constant.ControllerConstants.SUCCESS;

@RestController
@RequestMapping("/api")
@Slf4j
public class RestApis {
    private FriendsService friends;

    @Autowired
    public RestApis(FriendsService friendsService) {
        friends = friendsService;
    }

    @PostMapping("/connect")
    ResponseEntity connect(@RequestBody ConnectionRequest request) {
        try {
            friends.createConnection(request);
        } catch (BusinessException be) {
            log.error("Error in creating this connection", be);
            return FAILURE.apply(be.getMessage());
        }

        return SUCCESS; // no error means successful
    }
}
