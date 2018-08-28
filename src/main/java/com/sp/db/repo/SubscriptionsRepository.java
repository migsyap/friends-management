package com.sp.db.repo;

import com.sp.db.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionsRepository extends JpaRepository<Subscription, String> {
    Optional<List<Subscription>> findSubscriptionsByRequestorIgnoreCaseAndTargetIgnoreCase(String requestor, String target);

    Optional<List<Subscription>> findSubscriptionsByRequestorIgnoreCaseAndTargetIgnoreCaseAndBlockedIsTrue(String requestor, String target);
}
