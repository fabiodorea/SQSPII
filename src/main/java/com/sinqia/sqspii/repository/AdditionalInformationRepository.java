package com.sinqia.sqspii.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sinqia.sqspii.entity.AdditionalInformation;

public interface AdditionalInformationRepository  extends JpaRepository<AdditionalInformation, Long> {

    @Modifying
    @Query("DELETE FROM AdditionalInformation a where a.dynamicQrCode.id = :dynamicQrCodeID")
    void deleteByDynamic(Long dynamicQrCodeID);
    
}
