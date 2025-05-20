package org.greenflow.equipment.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "org.greenflow.equipment.output.persistent",
        entityManagerFactoryRef = "leasingEntityManagerFactory",
        transactionManagerRef = "leasingTransactionManager"
)
public class PostgresDataConfig {

    @Value("${datasource.leasing.ddl-auto}")
    private String hibernateDdlAutoMode;

    private static final String SHOW_SQL = "false";

    @Value("${datasource.leasing.url}")
    private String url;

    @Value("${datasource.leasing.username}")
    private String username;

    @Value("${datasource.leasing.password}")
    private String password;

    @Bean(name = "leasingDataSource")
    @ConfigurationProperties(prefix = "datasource.leasing")
    public DataSource leasingDataSource() {
        if (url != null && username != null && password != null) {
            return DataSourceBuilder.create()
                    .url(url)
                    .username(username)
                    .password(password)
                    .build();
        }
        throw new IllegalArgumentException("LeasingDataSource configuration is missing");
    }

    @Bean(name = "leasingEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean leasingEntityManagerFactory(
            @Qualifier("leasingDataSource") DataSource dataSource) {
        var entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setDataSource(dataSource);
        entityManager.setPackagesToScan("org.greenflow.equipment.model");
        entityManager.setPersistenceUnitName("leasingPersistentUnit");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManager.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = Map.of(
                "hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect",
                "hibernate.hbm2ddl.auto", hibernateDdlAutoMode,
                "hibernate.show_sql", SHOW_SQL,
                "hibernate.default_schema", "public",
                "databasePlatform", "org.hibernate.dialect.PostgreSQLDialect",
                "hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy"
        );
        entityManager.setJpaPropertyMap(properties);

        return entityManager;
    }

    @Bean(name = "leasingTransactionManager")
    public PlatformTransactionManager leasingTransactionManager(
            @Qualifier("leasingEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
