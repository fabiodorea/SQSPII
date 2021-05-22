package com.sinqia.sqspii.data.base.repository;


import com.sinqia.sqspii.data.base.entity.Teste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface TesteRepository  extends JpaRepository<Teste, Long> {
}
