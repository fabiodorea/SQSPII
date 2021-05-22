package com.sinqia.sqspii.data.multitenancy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sinqia.sqspii.data.multitenancy.entity.AuditDynamicQrCode;

public interface AuditDynamicQrCodeRepository  extends JpaRepository<AuditDynamicQrCode, Long> {

    
}
