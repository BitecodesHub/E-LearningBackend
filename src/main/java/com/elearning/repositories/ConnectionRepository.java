package com.elearning.repositories;

import com.elearning.models.Connection;
import com.elearning.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    @Query("SELECT c FROM Connection c WHERE c.receiver.id = :userId AND c.status = :status")
    List<Connection> findByReceiverAndStatus(Long userId, String status);

    @Query("SELECT c FROM Connection c WHERE (c.sender.id = :userId OR c.receiver.id = :userId) AND c.status = :status")
    List<Connection> findBySenderOrReceiverAndStatus(Long userId, String status);
}