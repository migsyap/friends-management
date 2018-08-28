package com.sp.db.repo;

import com.sp.db.entity.Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionsRepository extends JpaRepository<Connection, String> {
    Optional<List<Connection>> findConnectionsByEmail1IgnoreCaseAndEmail2IgnoreCase(String email1, String email2);

    List<Connection> findConnectionsByEmail1IgnoreCase(String email1);

    List<Connection> findConnectionsByEmail2IgnoreCase(String email2);
}
