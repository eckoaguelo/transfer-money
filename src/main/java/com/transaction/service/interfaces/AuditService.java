package com.transaction.service.interfaces;

public interface AuditService {
    void logTransaction(Long id, String functionName, String message);
}
