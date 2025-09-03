package se.sundsvall.messagingsettings.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import se.sundsvall.messagingsettings.Application;
import se.sundsvall.messagingsettings.service.MessagingSettingsService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class MessagingSettingsResourceFailureTest {

	@MockitoBean
	private MessagingSettingsService mockMessagingSettingsService;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getSenderInfoReturnsBadRequest() {
		final var response = webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/sender-info").build(Map.of("municipalityId", "9999")))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult().getResponseBody();

		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getViolations()).hasSize(1)
			.extracting(
				Violation::getField,
				Violation::getMessage)
			.contains(tuple(
				"getSenderInfo.municipalityId",
				"not a valid municipality ID"));

		verifyNoInteractions(mockMessagingSettingsService);
	}

	@Test
	void getCallbackEmailReturnsBadRequest() {
		final var response = webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/{departmentId}/callback-email").build(Map.of("municipalityId", "9999", "departmentId", " ")))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult().getResponseBody();

		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getViolations()).hasSize(2)
			.extracting(
				Violation::getField,
				Violation::getMessage)
			.containsExactlyInAnyOrder(
				tuple(
					"getCallbackEmail.municipalityId",
					"not a valid municipality ID"),
				tuple(
					"getCallbackEmail.departmentId",
					"must not be blank"));

		verifyNoInteractions(mockMessagingSettingsService);
	}

	@Test
	void getPortalSettingsReturnsBadRequest() {
		final var response = webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/portal-settings").build(Map.of("municipalityId", "9999")))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult().getResponseBody();

		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getViolations()).hasSize(1)
			.extracting(
				Violation::getField,
				Violation::getMessage)
			.contains(tuple(
				"getPortalSettings.municipalityId",
				"not a valid municipality ID"));

		verifyNoInteractions(mockMessagingSettingsService);
	}
}
