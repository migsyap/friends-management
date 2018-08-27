package com.sp.component;

import com.sp.db.entity.Connection;
import com.sp.db.repo.ConnectionsRepository;
import com.sp.dto.ConnectionRequest;
import com.sp.exception.BusinessException;
import com.sp.exception.ConnectionEstablishedException;
import com.sp.exception.InvalidConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Service
public class FriendsService {
    private ConnectionsRepository connection;

    @Autowired
    public FriendsService(ConnectionsRepository connectionsRepository) {
        connection = connectionsRepository;
    }

    public void createConnection(ConnectionRequest request) throws BusinessException {
        if (!request.isValidRequest()) throw new InvalidConnectionException();

        // there are no checks for string here because we have already established that the request is valid
        Function<String, Function<String, Optional<List<Connection>>>> findConnection = email1 -> email2 -> connection
                .findConnectionsByEmail1IgnoreCaseAndEmail2IgnoreCase(email1, email2)
                .filter(CollectionUtils::isNotEmpty);

        Optional<List<Connection>> forward = findConnection.apply(request.getEmail1()).apply(request.getEmail2());
        Optional<List<Connection>> reverse = findConnection.apply(request.getEmail2()).apply(request.getEmail1());

        if (forward.isPresent() || reverse.isPresent())
            throw new ConnectionEstablishedException(request.getEmail1(), request.getEmail2());

        Date now = new Date();

        connection.save(Connection.builder()
                .id(UUID.randomUUID().toString())
                .email1(request.getEmail1())
                .email2(request.getEmail2())
                .createdOn(now)
                .modifiedOn(now)
                .build());
    }
}
