package com.sinqia.sqspii.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sinqia.sqspii.entity.Situation;

public interface SituationRepository  extends JpaRepository<Situation, Long> {

    @Query("FROM Situation s WHERE s.description = :description ")
    Optional<Situation> findByDescription(@Param("description") String description);
    
}
