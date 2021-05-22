package com.sinqia.sqspii.data.multitenancy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sinqia.sqspii.data.multitenancy.entity.UsuarioDadosAcesso;

public interface UsuarioDadosAcessoRepository extends JpaRepository<UsuarioDadosAcesso, Long> {
    UsuarioDadosAcesso findByUserCode(String userCode);
}