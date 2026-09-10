package se.sundsvall.messagingsettings.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * Gives the resource handlers precedence over the request mappings.
 * <p>
 * The API maps /{municipalityId} and /{municipalityId}/{id} at the root, and request mappings are matched before
 * resource mappings. Without this, requests for the Swagger UI resources under /swagger-ui/ are handled by
 * MessagingSettingsResource and rejected with a constraint violation instead of being served.
 * <p>
 * Static resource mappings are disabled in dept44 (spring.web.resources.add-mappings=false), so the registry holds
 * nothing but the Swagger UI resources registered by springdoc.
 */
@Configuration
class SwaggerUiConfiguration implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		registry.setOrder(HIGHEST_PRECEDENCE);
	}
}
