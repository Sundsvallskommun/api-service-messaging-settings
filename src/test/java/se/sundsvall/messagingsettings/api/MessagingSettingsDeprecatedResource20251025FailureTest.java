package se.sundsvall.messagingsettings.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import se.sundsvall.dept44.support.Identifier;
import se.sundsvall.messagingsettings.Application;
import se.sundsvall.messagingsettings.service.MessagingSettingsService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class MessagingSettingsDeprecatedResource20251025FailureTest {

	@MockitoBean
	private MessagingSettingsService mockMessagingSettingsService;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void fetchMessagingSettingsBadMunicipalityInPath() {
		final var response = webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}").build(Map.of("municipalityId", "9999")))
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
				"fetchMessagingSettings.municipalityId",
				"not a valid municipality ID"));

		verifyNoInteractions(mockMessagingSettingsService);
	}

	@Test
	void getMessagingSettingsForUserBadMunicipalityInPath() {
		final var response = webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/user").build(Map.of("municipalityId", "9999")))
			.header(Identifier.HEADER_NAME, "joe01doe; type=adAccount")
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
				"getMessagingSettingsForUser.municipalityId",
				"not a valid municipality ID"));

		verifyNoInteractions(mockMessagingSettingsService);
	}

	@Test
	void getMessagingSettingsForUserMissingHeader() {
		final var response = webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/user").build(Map.of("municipalityId", "2281")))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(Problem.class)
			.returnResult().getResponseBody();

		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getTitle()).isEqualTo("Bad Request");
		assertThat(response.getDetail()).isEqualTo("Required request header 'X-Sent-By' for method parameter type String is not present");

		verifyNoInteractions(mockMessagingSettingsService);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"", "username", "type=adAccount", ";type=adAccount", "username;"
	})
	void getMessagingSettingsForUserBadHeader(String identifierValue) {
		final var response = webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/user").build(Map.of("municipalityId", "2281")))
			.header(Identifier.HEADER_NAME, identifierValue)
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
			.containsExactlyInAnyOrder(tuple(
				"getMessagingSettingsForUser.xSentBy",
				"X-Sent-By must be provided and must be in the correct format [type=TYPE; VALUE]"));

		verifyNoInteractions(mockMessagingSettingsService);
	}
}
