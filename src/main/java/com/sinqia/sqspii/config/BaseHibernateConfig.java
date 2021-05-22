package com.sinqia.sqspii.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "painelEntityManagerFactory", transactionManagerRef = "painelTransactionManager", basePackages = {"com.sinqia.sqspii*"})
public class BaseHibernateConfig {
    @Autowired
    private JpaProperties jpaProperties;

    @Autowired
    private org.springframework.core.env.Environment env;

    @Bean
    JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean(name = "painelEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean painelEntityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("base") DataSource dataSource) {
        return builder.dataSource(dataSource).packages("com.sinqia.sqspii*").persistenceUnit("base").build();
    }

    @Bean(name = "base")
    @ConfigurationProperties(prefix = "spring.datasource-base")
    public DataSource dataSource3(org.springframework.core.env.Environment env) {
        return DataSourceBuilder.create()
                .url(env.getRequiredProperty("spring.datasource.url"))
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "painelTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("painelEntityManagerFactory") EntityManagerFactory painelEntityManagerFactory) {
        return new JpaTransactionManager(painelEntityManagerFactory);
    }
}