package dev.makeev.coworking_service_app.config;

import dev.makeev.coworking_service_app.util.InitDb;
import dev.makeev.coworking_service_app.util.YamlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@PropertySource(value = "classpath:properties.yml", factory = YamlPropertySourceFactory.class)
public class DatabaseConfig {

    @Value("${db.driver}")
    private String driver;

    @Value("${db.url}")
    private String url;

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;

    @Value("${liquibase.defaultSchemaName}")
    private String schemaName;

    @Value("${liquibase.changelogFile}")
    private String changelog;

    @Bean
    public DataSource dataSource()  {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    public InitDb initializeDatabase() {
        InitDb initDb = new InitDb(dataSource(), schemaName, changelog);
        initDb.initDb();
        return initDb;
    }
}