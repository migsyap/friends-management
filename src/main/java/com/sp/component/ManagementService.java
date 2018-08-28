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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class ManagementService {
    private ConnectionsRepository connections;
    private SubscriptionsRepository subscriptions;

    @Autowired
    public ManagementService(ConnectionsRepository connectionsRepository, SubscriptionsRepository subscriptionsRepository) {
        connections = connectionsRepository;
        subscriptions = subscriptionsRepository;
    }

    public void createConnection(ConnectionRequest request) throws BusinessException {
        if (!request.isValidRequest()) throw new InvalidConnectionException();

        // there are no checks for string here because we have already established that the request is valid
        Function<String, Function<String, Optional<List<Connection>>>> findConnection = email1 -> email2 -> connections
                .findConnectionsByEmail1IgnoreCaseAndEmail2IgnoreCase(email1, email2)
                .filter(CollectionUtils::isNotEmpty);

        Optional<List<Connection>> forward = findConnection.apply(request.getEmail1()).apply(request.getEmail2());
        Optional<List<Connection>> reverse = findConnection.apply(request.getEmail2()).apply(request.getEmail1());

        if (forward.isPresent() || reverse.isPresent())
            throw new ConnectionEstablishedException(request.getEmail1(), request.getEmail2());

        Function<String, Predicate<String>> isBlocked = requestor -> target -> subscriptions
                .findSubscriptionsByRequestorIgnoreCaseAndTargetIgnoreCaseAndBlockedIsTrue(requestor, target)
                .filter(CollectionUtils::isNotEmpty).isPresent();

        if (isBlocked.apply(request.getEmail1()).test(request.getEmail2()))
            throw new BlockedException(request.getEmail1(), request.getEmail2());

        if (isBlocked.apply(request.getEmail2()).test(request.getEmail1()))
            throw new BlockedException(request.getEmail2(), request.getEmail1());

        Date now = new Date();

        connections.save(Connection.builder()
                .id(UUID.randomUUID().toString())
                .email1(request.getEmail1())
                .email2(request.getEmail2())
                .createdOn(now)
                .modifiedOn(now)
                .build());
    }

    public List<String> findConnections(FindConnectionsRequest request) {
        List<String> forward = Optional.ofNullable(connections.findConnectionsByEmail1IgnoreCase(request.getEmail())).orElse(Collections.emptyList())
                .stream().map(Connection::getEmail2).collect(Collectors.toList());
        List<String> reverse = Optional.ofNullable(connections.findConnectionsByEmail2IgnoreCase(request.getEmail())).orElse(Collections.emptyList())
                .stream().map(Connection::getEmail1).collect(Collectors.toList());

        return Stream.of(forward, reverse).flatMap(List::stream).distinct().collect(Collectors.toList());
    }

    public List<String> findCommonConnections(ConnectionRequest request) throws BusinessException {
        if (!request.isValidRequest()) throw new InvalidConnectionException();

        return new ArrayList<>(CollectionUtils.intersection(
                findConnections(FindConnectionsRequest.builder().email(request.getEmail1()).build()),
                findConnections(FindConnectionsRequest.builder().email(request.getEmail2()).build())
        ));
    }

    public void subscribe(SubscriptionRequest request) throws BusinessException {
        if (!request.isValidRequest()) throw new InvalidSubscriptionException();

        // there are no checks for string here because we have already established that the request is valid
        Optional<List<Subscription>> subs = subscriptions
                .findSubscriptionsByRequestorIgnoreCaseAndTargetIgnoreCase(request.getRequestor(), request.getTarget())
                .filter(CollectionUtils::isNotEmpty);

        if (subs.isPresent()) throw new SubscribedException(request.getRequestor(), request.getTarget());

        Date now = new Date();

        subscriptions.save(Subscription.builder()
                .id(UUID.randomUUID().toString())
                .requestor(request.getRequestor())
                .target(request.getTarget())
                .blocked(false)
                .createdOn(now)
                .modifiedOn(now)
                .build());
    }

    public void block(SubscriptionRequest request) throws BusinessException {
        if (!request.isValidRequest()) throw new InvalidSubscriptionException();

        // there are no checks for string here because we have already established that the request is valid
        Subscription subscription = subscriptions
                .findSubscriptionsByRequestorIgnoreCaseAndTargetIgnoreCase(request.getRequestor(), request.getTarget())
                .filter(CollectionUtils::isNotEmpty)
                .orElse(Collections.emptyList())
                .stream().findFirst().orElse(Subscription.builder().build());

        Date now = new Date();

        subscriptions.save(Subscription.builder()
                .id(StringUtils.defaultIfBlank(subscription.getId(), UUID.randomUUID().toString()))
                .requestor(request.getRequestor())
                .target(request.getTarget())
                .blocked(true)
                .createdOn(Optional.ofNullable(subscription.getCreatedOn()).orElse(now))
                .modifiedOn(now)
                .build());
    }

    public List<String> findRecipientsOfUpdate(UpdateRequest request) throws BusinessException {
        if (!request.isValidRequest()) throw new InvalidUpdateException();

        List<String> friends = findConnections(FindConnectionsRequest.builder().email(request.getSender()).build());

        List<String> followers = subscriptions.findSubscriptionsByTargetIgnoreCaseAndBlockedIsFalse(request.getSender())
                .filter(CollectionUtils::isNotEmpty)
                .orElse(Collections.emptyList())
                .stream().map(Subscription::getRequestor).collect(Collectors.toList());

        List<String> blockedList = Stream.of(
                Optional.ofNullable(subscriptions.findSubscriptionsByRequestorIgnoreCaseAndBlockedIsTrue(request.getSender())).orElse(Collections.emptyList()).stream().map(Subscription::getTarget).collect(Collectors.toList()),
                Optional.ofNullable(subscriptions.findSubscriptionsByTargetIgnoreCaseAndBlockedIsTrue(request.getSender())).orElse(Collections.emptyList()).stream().map(Subscription::getRequestor).collect(Collectors.toList())
        ).flatMap(List::stream).collect(Collectors.toList());

        return new ArrayList<>(CollectionUtils.removeAll(
                Stream.of(friends, followers, request.getMentions()).flatMap(List::stream).distinct().collect(Collectors.toList()),
                blockedList
        ));
    }
}
