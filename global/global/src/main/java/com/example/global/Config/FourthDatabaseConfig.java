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
    basePackages = "com.example.global.repository.fourth",
    entityManagerFactoryRef = "fourthEntityManagerFactory",
    transactionManagerRef = "fourthTransactionManager"
)
public class FourthDatabaseConfig {

    @Bean(name = "fourthDataSourceProperties")
    @ConfigurationProperties("spring.datasource.fourth")
    public DataSourceProperties fourthDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "fourthDataSource")
    public DataSource fourthDataSource() {
        return fourthDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "fourthEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean fourthEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        properties.put("hibernate.hbm2ddl.auto", "update");

        return builder
            .dataSource(fourthDataSource())
            .packages("com.example.global.entity", "com.example.global.entity.fourth")
            .persistenceUnit("fourthDb")
            .properties(properties)
            .build();
    }

    @Bean(name = "fourthTransactionManager")
    public PlatformTransactionManager fourthTransactionManager(EntityManagerFactory fourthEntityManagerFactory) {
        return new JpaTransactionManager(fourthEntityManagerFactory);
    }
}
