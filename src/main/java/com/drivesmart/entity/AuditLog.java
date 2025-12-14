package com.drivesmart.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String user;

    @Column(nullable = false)
    private String action;

    @Column(length = 1000)
    private String details;

    @Column
    private String ip;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditLevel level;

    @PrePersist
    void onCreate() {
        if (timestamp == null) timestamp = LocalDateTime.now();
        if (level == null) level = AuditLevel.SUCCESS;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public AuditLevel getLevel() { return level; }
    public void setLevel(AuditLevel level) { this.level = level; }
}
