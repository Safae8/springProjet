package com.ApplicationGoogleDrive.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_requests")
@Data
public class AccessRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private File file;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    private String message;

    private LocalDateTime requestedAt;
    private LocalDateTime respondedAt;

    @PrePersist
    protected void onCreate() {
        requestedAt = LocalDateTime.now();
    }

    public void setRequester(User requester) {
    }

    public void setFile(File file) {
    }

    public void setMessage(String message) {
    }

    public void setStatus(RequestStatus requestStatus) {
    }

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}