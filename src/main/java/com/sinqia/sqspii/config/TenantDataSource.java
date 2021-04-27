package com.sinqia.sqspii.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

import com.sinqia.sqspii.entity.UsuarioDadosAcesso;
import com.sinqia.sqspii.repository.UsuarioDadosAcessoRepository;

@Component
public class TenantDataSource implements Serializable {

    private static final long serialVersionUID = 2404513094994617404L;

    private HashMap<String, DataSource> dataSources = new HashMap<>();

    @Autowired
    private UsuarioDadosAcessoRepository configRepo;

    public DataSource getDataSource(String name) {
        if (dataSources.get(name) != null) {
            return dataSources.get(name);
        }
        DataSource dataSource = createDataSource(name);
        if (dataSource != null) {
            dataSources.put(name, dataSource);
        }
        return dataSource;
    }

    @PostConstruct
    public Map<String, DataSource> getAll() {
        List<UsuarioDadosAcesso> configList = configRepo.findAll();
        Map<String, DataSource> result = new HashMap<>();
        for (UsuarioDadosAcesso config : configList) {
            DataSource dataSource = getDataSource(config.getUserCode());
            result.put(config.getUserCode(), dataSource);
        }
        return result;
    }

    private DataSource createDataSource(String name) {
        UsuarioDadosAcesso config = configRepo.findByUserCode(name);
        if (config != null) {
            DataSourceBuilder<?> factory = DataSourceBuilder
                    .create().driverClassName(config.getDriverClassName())
                    .username(config.getDbUserName())
                    .password(config.getDbPassword())
                    .url(config.getUrl());
            DataSource ds = factory.build();
            return ds;
        }
        return null;
    }

}