package com.example.global.Config;

import jakarta.persistence.EntityManagerFactory; // ✅ Change to javax
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.example.global.repository.primary",
    entityManagerFactoryRef = "primaryEntityManagerFactory",
    transactionManagerRef = "primaryTransactionManager"
)
public class PrimaryDatabaseConfig {

    @Primary
    @Bean(name = "primaryDataSourceProperties")
    @ConfigurationProperties("spring.datasource.primary")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "primaryDataSource")
    public DataSource primaryDataSource() {
        return primaryDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean(name = "primaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        properties.put("hibernate.hbm2ddl.auto", "update");

        return builder
            .dataSource(primaryDataSource())
            .packages("com.example.global.entity", "com.example.global.entity.primary") // ✅ Use correct package for your entities
            .persistenceUnit("primaryDb")
            .properties(properties)
            .build();
    }

    @Primary
    @Bean(name = "primaryTransactionManager")
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier("primaryEntityManagerFactory") EntityManagerFactory entityManagerFactory) { // ✅ Add @Qualifier
        return new JpaTransactionManager(entityManagerFactory);
    }
}
