package com.sinqia.sqspii.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sinqia.sqspii.entity.Parameter;

public interface ParameterRepository  extends JpaRepository<Parameter, Long> {

    
}
