package se.sundsvall.messagingsettings.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.messagingsettings.Application;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.TEXT_HTML;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class SwaggerUiConfigurationTest {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void swaggerUiEntryPointRedirectsToIndexPage() {
		webTestClient.get().uri("/swagger-ui.html")
			.exchange()
			.expectStatus().isFound()
			.expectHeader().valueEquals(LOCATION, "/swagger-ui/index.html");
	}

	@Test
	void swaggerUiResourcesAreServedInsteadOfMatchingTheApi() {
		webTestClient.get().uri("/swagger-ui/index.html")
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentTypeCompatibleWith(TEXT_HTML);

		webTestClient.get().uri("/swagger-ui/swagger-initializer.js")
			.exchange()
			.expectStatus().isOk();
	}

	@Test
	void openApiSpecificationIsServedInsteadOfMatchingTheApi() {
		webTestClient.get().uri("/api-docs")
			.exchange()
			.expectStatus().isOk()
			.expectBody().jsonPath("$.info.title").isEqualTo("api-service-messaging-settings");
	}
}
