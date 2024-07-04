package dev.makeev.coworking_service_app.servlet;

import dev.makeev.coworking_service_app.util.InitDb;
import dev.makeev.coworking_service_app.util.implementation.ConnectionManagerImpl;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.postgresql.Driver;

import java.sql.SQLException;

@WebListener
public class MyServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        new InitDb(new ConnectionManagerImpl()).initDb();
    }
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            Driver.deregister();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}