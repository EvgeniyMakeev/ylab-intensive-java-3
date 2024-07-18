package dev.makeev.coworking_service_app;

import dev.makeev.coworking_service_app.util.AuthFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

/**
 * Configures the Spring MVC web application using Java configuration.
 */
public class MyWebApplicationInitializer implements WebApplicationInitializer {

    /**
     * Initialize the Spring MVC web application.
     *
     * @param servletContext the ServletContext of the web application
     */
    @Override
    public void onStartup(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(servletContext);
        context.scan("dev.makeev");
        context.refresh();

        ServletRegistration.Dynamic registration = servletContext.addServlet("dispatcher", new DispatcherServlet(context));
        registration.setLoadOnStartup(1);
        registration.addMapping("/");

        FilterRegistration.Dynamic authFilter = servletContext.addFilter("authFilter", context.getBean(AuthFilter.class));
        authFilter.addMappingForUrlPatterns(null, false, "/api/v1/*");
    }
}
