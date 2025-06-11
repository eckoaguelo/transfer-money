package com.transaction.repository;

import com.transaction.entity.AuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<AuditTrail, Long> {

}

