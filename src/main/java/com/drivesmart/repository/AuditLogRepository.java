package com.drivesmart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drivesmart.entity.AuditLevel;
import com.drivesmart.entity.AuditLog;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findAllByOrderByTimestampDesc();
    List<AuditLog> findByLevelOrderByTimestampDesc(AuditLevel level);
    List<AuditLog> findByUsernameContainingIgnoreCaseOrderByTimestampDesc(String username);
}
