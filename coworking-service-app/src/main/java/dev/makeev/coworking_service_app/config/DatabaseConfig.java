package dev.makeev.coworking_service_app.config;

import dev.makeev.coworking_service_app.util.InitDb;
import dev.makeev.coworking_service_app.util.YamlPropertySourceFactory;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * Configuration class for setting up the database using Spring's BasicDataSource.
 * Reads database configuration properties from 'properties.yml' using YAML format.
 */
@Configuration
@PropertySource(value = "classpath:properties.yml", factory = YamlPropertySourceFactory.class)
public class DatabaseConfig {

    private final Environment env;

    @Autowired
    public DatabaseConfig(Environment env) {
        this.env = env;
    }

    /**
     * Creates a BasicDataSource bean configured with database connection details.
     *
     * @return Configured BasicDataSource instance.
     */
    @Bean
    public BasicDataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("db.driver"));
        dataSource.setUrl(env.getProperty("db.url"));
        dataSource.setUsername(env.getProperty("db.username"));
        dataSource.setPassword(env.getProperty("db.password"));
        dataSource.setInitialSize(3);
        dataSource.setMaxTotal(10);
        return dataSource;
    }

    /**
     * Initializes the database using Liquibase based on configuration properties.
     *
     * @return Initialized InitDb instance.
     */
    @Bean
    public InitDb initializeDatabase() {
        InitDb initDb = new InitDb(
                dataSource(),
                env.getProperty("liquibase.defaultSchemaName"),
                env.getProperty("liquibase.changelogFile"));

        initDb.initDb();
        return initDb;
    }
}
