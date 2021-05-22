package com.sinqia.sqspii.data.multitenancy.repository;

import com.sinqia.sqspii.data.multitenancy.entity.DigitalCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DigitalCertificateRepository extends JpaRepository<DigitalCertificate, Long> {

    @Query(value = "select top 1 * from spi_certificado_digital where nom_hst_ste_qrc = :host", nativeQuery = true)
    Optional<DigitalCertificate> findByHost(@Param("host") String host);
}
