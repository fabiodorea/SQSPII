package com.sinqia.sqspii.data.multitenancy.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sinqia.sqspii.data.multitenancy.entity.DynamicQrCode;

public interface DynamicQrCodeRepository extends JpaRepository<DynamicQrCode, Long> {

    @Query("FROM DynamicQrCode d WHERE d.payloadIdentifier = :payloadIdentifier ")
    Optional<DynamicQrCode> findByPayloadIdentifier(@Param("payloadIdentifier") UUID payloadIdentifier);
}
