package dev.makeev.coworking_service_app.util;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.util.Properties;

/**
 * Custom Spring PropertySourceFactory for loading YAML configuration files as PropertySource.
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

    /**
     * Create a PropertySource instance from the provided YAML resource.
     *
     * @param name     the name of the property source, or {@code null} if not specified
     * @param resource the YAML resource to load
     * @return a PropertySource instance containing properties loaded from the YAML file
     */
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) {
        Properties propertiesFromYaml = loadYamlIntoProperties(resource);
        String sourceName = name != null ? name : resource.getResource().getFilename();
        return new PropertiesPropertySource(sourceName, propertiesFromYaml);
    }

    /**
     * Load YAML content into a Properties object.
     *
     * @param resource the encoded resource containing the YAML content
     * @return a Properties object populated with YAML content
     */
    private Properties loadYamlIntoProperties(EncodedResource resource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());
        factory.afterPropertiesSet();
        return factory.getObject();
    }
}
