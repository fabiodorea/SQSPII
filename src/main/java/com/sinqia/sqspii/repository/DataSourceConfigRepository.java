package com.sinqia.sqspii.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sinqia.sqspii.entity.DataSourceConfig;

public interface DataSourceConfigRepository extends JpaRepository<DataSourceConfig, Long> {
    DataSourceConfig findByName(String name);
}