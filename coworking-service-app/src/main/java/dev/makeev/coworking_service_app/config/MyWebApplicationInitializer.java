package dev.makeev.coworking_service_app.config;

import dev.makeev.coworking_service_app.util.InitDb;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class MyWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.scan("dev.makeev");

        ServletRegistration.Dynamic registration = servletContext.addServlet("app", new DispatcherServlet(context));
        registration.setLoadOnStartup(1);
        registration.addMapping("/api/*");

        new InitDb().initDb();
    }
}
