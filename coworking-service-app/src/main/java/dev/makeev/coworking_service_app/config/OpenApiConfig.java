package dev.makeev.coworking_service_app.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI documentation setup.
 */
@SecurityScheme(
        name = "apiKeyScheme",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "Authorization"
)
@Configuration
public class OpenApiConfig {

    /**
     * Configures OpenAPI bean for API documentation.
     *
     * @return Configured OpenAPI instance.
     */
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Coworking Service API")
                        .description("The Coworking Service Application is program " +
                                "to manage bookings and spaces in a coworking environment.")
                        .version("v1")
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}
