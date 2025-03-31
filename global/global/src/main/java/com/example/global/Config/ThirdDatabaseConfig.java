package com.example.global.Config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.example.global.repository.third",
    entityManagerFactoryRef = "thirdEntityManagerFactory",
    transactionManagerRef = "thirdTransactionManager"
)
public class ThirdDatabaseConfig {

    @Bean(name = "thirdDataSourceProperties")
    @ConfigurationProperties("spring.datasource.third")
    public DataSourceProperties thirdDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "thirdDataSource")
    public DataSource thirdDataSource() {
        return thirdDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "thirdEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean thirdEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        properties.put("hibernate.hbm2ddl.auto", "update");

        return builder
            .dataSource(thirdDataSource())
            .packages("com.example.global.entity", "com.example.global.entity.third")
            .persistenceUnit("thirdDb")
            .properties(properties)
            .build();
    }

    @Bean(name = "thirdTransactionManager")
    public PlatformTransactionManager thirdTransactionManager(EntityManagerFactory thirdEntityManagerFactory) {
        return new JpaTransactionManager(thirdEntityManagerFactory);
    }
}