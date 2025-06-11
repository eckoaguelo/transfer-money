package com.transaction.service;

import com.transaction.entity.AuditTrail;
import com.transaction.repository.AuditRepository;
import com.transaction.service.interfaces.AuditService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class AuditServiceImpl implements AuditService {
    private final AuditRepository auditRepository;

    public AuditServiceImpl(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public void logTransaction(Long id, String functionName, String message) {
        AuditTrail audit = new AuditTrail();
        audit.setAccountId(id);
        audit.setFunctionName(functionName);
        audit.setAuditMessage(message);
        audit.setDateTime(new Timestamp(System.currentTimeMillis()));

        auditRepository.save(audit);
    }
}
