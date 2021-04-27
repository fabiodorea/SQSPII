package com.sinqia.sqspii.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sinqia.sqspii.entity.UsuarioDadosAcesso;

public interface UsuarioDadosAcessoRepository extends JpaRepository<UsuarioDadosAcesso, Long> {
    UsuarioDadosAcesso findByUserCode(String userCode);
}