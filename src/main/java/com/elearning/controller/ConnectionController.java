package com.elearning.controller;

import com.elearning.models.Connection;
import com.elearning.models.User;
import com.elearning.repositories.ConnectionRepository;
import com.elearning.repositories.UserRepository;
import com.elearning.response_request.ConnectionRequest;
import com.elearning.response_request.ConnectionUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/connections")
public class ConnectionController {
    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private UserRepository userRepository;

    // Send connection request
    @PostMapping
    public ResponseEntity<Connection> sendConnectionRequest(@RequestBody ConnectionRequest request) {
        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Connection connection = new Connection();
        connection.setSender(sender);
        connection.setReceiver(receiver);
        connection.setStatus("PENDING");
        connectionRepository.save(connection);
        return ResponseEntity.ok(connection);
    }

    // Get pending requests for a user
    @GetMapping("/pending/{userId}")
    public List<Connection> getPendingRequests(@PathVariable Long userId) {
        return connectionRepository.findByReceiverAndStatus(userId, "PENDING");
    }

    // Accept/Reject connection
    @PutMapping("/{id}")
    public ResponseEntity<Connection> updateConnection(@PathVariable Long id, @RequestBody ConnectionUpdate update) {
        Connection connection = connectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Connection not found"));
        connection.setStatus(update.getStatus());
        connectionRepository.save(connection);
        return ResponseEntity.ok(connection);
    }
}