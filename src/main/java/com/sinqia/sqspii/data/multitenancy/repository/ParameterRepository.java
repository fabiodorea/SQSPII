package com.sinqia.sqspii.data.multitenancy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sinqia.sqspii.data.multitenancy.entity.Parameter;

public interface ParameterRepository  extends JpaRepository<Parameter, Long> {

    
}
