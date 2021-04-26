package com.sinqia.sqspii.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sinqia.sqspii.entity.DynamicQrCode;

public interface DynamicQrCodeRepository extends JpaRepository<DynamicQrCode, Long> {

    @Query("FROM DynamicQrCode d WHERE d.payloadIdentifier = :payloadIdentifier ")
    Optional<DynamicQrCode> findByPayloadIdentifier(@Param("payloadIdentifier") UUID payloadIdentifier);
}
