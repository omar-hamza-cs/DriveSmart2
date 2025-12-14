package com.drivesmart.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.drivesmart.entity.AuditLevel;
import com.drivesmart.entity.AuditLog;
import com.drivesmart.repository.AuditLogRepository;

@Service
public class AuditLogService {

    private final AuditLogRepository repo;

    public AuditLogService(AuditLogRepository repo) {
        this.repo = repo;
    }

    public List<AuditLog> getAuditLogs(String search, AuditLevel level, int page, int size) {
        List<AuditLog> base = repo.findAllByOrderByTimestampDesc();
        if (level != null) {
            base = base.stream().filter(l -> Objects.equals(l.getLevel(), level)).collect(Collectors.toList());
        }
        if (search != null && !search.isBlank()) {
            String q = search.toLowerCase();
            base = base.stream().filter(l ->
                    (l.getUser() != null && l.getUser().toLowerCase().contains(q)) ||
                    (l.getAction() != null && l.getAction().toLowerCase().contains(q)) ||
                    (l.getDetails() != null && l.getDetails().toLowerCase().contains(q))
            ).collect(Collectors.toList());
        }
        int from = Math.max(0, page * size);
        int to = Math.min(base.size(), from + size);
        if (from >= to) return java.util.Collections.emptyList();
        return base.subList(from, to);
    }
}
