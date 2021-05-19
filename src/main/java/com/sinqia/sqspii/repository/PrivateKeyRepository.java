package com.sinqia.sqspii.repository;

import com.sinqia.sqspii.entity.VaultPrivateKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PrivateKeyRepository extends JpaRepository<VaultPrivateKey, Long> {

    @Query(value = "FROM VaultPrivateKey pk join fetch pk.digitalCertificate dc WHERE dc.idEntPar = :id_ent_par and dc.idSitCer = 2 and dc.idFnaCerDig = 3")
    Optional<VaultPrivateKey> findPrivateKey(@Param("id_ent_par") Long id_ent_par);
}
