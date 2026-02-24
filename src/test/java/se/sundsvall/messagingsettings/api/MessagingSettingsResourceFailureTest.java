package se.sundsvall.messagingsettings.api;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.problem.violations.Violation;
import se.sundsvall.messagingsettings.Application;
import se.sundsvall.messagingsettings.api.model.MessagingSettingsRequest;
import se.sundsvall.messagingsettings.service.MessagingSettingsService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class MessagingSettingsResourceFailureTest {

	@MockitoBean
	private MessagingSettingsService messagingSettingsServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void fetchMessagingSettingsWithFaultyMunicipalityId() {
		final var response = webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}").build(Map.of("municipalityId", "9999")))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult().getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getViolations()).hasSize(1)
			.extracting(
				Violation::field,
				Violation::message)
			.contains(tuple(
				"fetchMessagingSettings.municipalityId",
				"not a valid municipality ID"));

		verifyNoInteractions(messagingSettingsServiceMock);
	}

	@Test
	void fetchMessagingSettingsForUserWithFaultyMunicipalityId() {
		final var response = webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/user").build(Map.of("municipalityId", "9999")))
			.header("x-sent-by", "joe01doe;type=adAccount")
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult().getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getViolations()).hasSize(1)
			.extracting(
				Violation::field,
				Violation::message)
			.contains(tuple(
				"getMessagingSettingsForUser.municipalityId",
				"not a valid municipality ID"));

		verifyNoInteractions(messagingSettingsServiceMock);
	}

	@Test
	void fetchMessagingSettingsForUserWithFaultyIdentifier() {
		final var response = webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/user").build(Map.of("municipalityId", "2281")))
			.header("x-sent-by", "invalid")
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult().getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getViolations()).hasSize(1)
			.extracting(
				Violation::field,
				Violation::message)
			.contains(tuple(
				"getMessagingSettingsForUser.xSentBy",
				"X-Sent-By must be provided and must be in the correct format [type=TYPE; VALUE]"));

		verifyNoInteractions(messagingSettingsServiceMock);
	}

	@Test
	void fetchMessagingSettingsForUserWithMissingIdentifier() {
		final var response = webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/user").build(Map.of("municipalityId", "2281")))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(Problem.class)
			.returnResult().getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getTitle()).isEqualTo("Bad Request");
		assertThat(response.getDetail()).isEqualTo("Required header 'X-Sent-By' is not present.");

		verifyNoInteractions(messagingSettingsServiceMock);
	}

	@Test
	void createMessagingSettingWithInvalidMunicipalityId() {
		// Arrange
		final var invalidMunicipalityId = "invalid";
		final var request = MessagingSettingsRequest.builder()
			.withValues(List.of(
				MessagingSettingsRequest.MessagingSettingValueRequest.builder()
					.withKey("key")
					.withValue("value")
					.withType("STRING")
					.build()))
			.build();

		// Act & Assert
		webTestClient.post()
			.uri("/{municipalityId}", invalidMunicipalityId)
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(messagingSettingsServiceMock);
	}

	@Test
	void createMessagingSettingWithEmptyValues() {
		// Arrange
		final var municipalityId = "2281";
		final var request = MessagingSettingsRequest.builder()
			.withValues(List.of())
			.build();

		// Act & Assert
		webTestClient.post()
			.uri("/{municipalityId}", municipalityId)
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(messagingSettingsServiceMock);
	}

	@Test
	void createMessagingSettingWithNullValues() {
		// Arrange
		final var municipalityId = "2281";
		final var request = Map.of(); // Empty request body

		// Act & Assert
		webTestClient.post()
			.uri("/{municipalityId}", municipalityId)
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(messagingSettingsServiceMock);
	}

	@Test
	void createMessagingSettingWithMissingRequestBody() {
		// Arrange
		final var municipalityId = "2281";

		// Act & Assert
		webTestClient.post()
			.uri("/{municipalityId}", municipalityId)
			.contentType(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(messagingSettingsServiceMock);
	}

	@Test
	void getMessagingSettingByIdNotFound() {
		// Arrange
		final var municipalityId = "2281";
		final var id = "a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d";

		when(messagingSettingsServiceMock.getMessagingSettingById(municipalityId, id))
			.thenThrow(Problem.valueOf(NOT_FOUND, "Messaging setting not found for municipality with ID '" + municipalityId + "' and ID '" + id + "'."));

		// Act & Assert
		webTestClient.get()
			.uri("/{municipalityId}/{id}", municipalityId, id)
			.exchange()
			.expectStatus().isNotFound()
			.expectBody()
			.jsonPath("$.title").isEqualTo("Not Found")
			.jsonPath("$.status").isEqualTo(404);
	}

	@Test
	void getMessagingSettingByIdWithInvalidUuid() {
		// Arrange
		final var municipalityId = "2281";
		final var invalidId = "not-a-uuid";

		// Act & Assert
		webTestClient.get()
			.uri("/{municipalityId}/{id}", municipalityId, invalidId)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(messagingSettingsServiceMock);
	}

	@Test
	void getMessagingSettingByIdWithInvalidMunicipalityId() {
		// Arrange
		final var invalidMunicipalityId = "invalid";
		final var id = "123e4567-e89b-12d3-a456-426614174000";

		// Act & Assert
		webTestClient.get()
			.uri("/{municipalityId}/{id}", invalidMunicipalityId, id)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(messagingSettingsServiceMock);
	}

	@Test
	void updateMessagingSettingNotFound() {
		// Arrange
		final var municipalityId = "2281";
		final var id = "a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d";
		final var request = MessagingSettingsRequest.builder()
			.withValues(List.of(
				MessagingSettingsRequest.MessagingSettingValueRequest.builder()
					.withKey("key")
					.withValue("value")
					.withType("STRING")
					.build()))
			.build();

		when(messagingSettingsServiceMock.updateMessagingSetting(eq(municipalityId), eq(id), any(MessagingSettingsRequest.class)))
			.thenThrow(Problem.valueOf(NOT_FOUND, "Messaging setting not found for municipality with ID '" + municipalityId + "' and ID '" + id + "'."));

		// Act & Assert
		webTestClient.patch()
			.uri("/{municipalityId}/{id}", municipalityId, id)
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isNotFound()
			.expectBody()
			.jsonPath("$.title").isEqualTo("Not Found")
			.jsonPath("$.status").isEqualTo(404);
	}

	@Test
	void updateMessagingSettingWithInvalidMunicipalityId() {
		// Arrange
		final var invalidMunicipalityId = "invalid";
		final var id = "123e4567-e89b-12d3-a456-426614174000";
		final var request = MessagingSettingsRequest.builder()
			.withValues(List.of(
				MessagingSettingsRequest.MessagingSettingValueRequest.builder()
					.withKey("key")
					.withValue("value")
					.withType("STRING")
					.build()))
			.build();

		// Act & Assert
		webTestClient.patch()
			.uri("/{municipalityId}/{id}", invalidMunicipalityId, id)
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(messagingSettingsServiceMock);
	}

	@Test
	void updateMessagingSettingWithInvalidUuid() {
		// Arrange
		final var municipalityId = "2281";
		final var invalidId = "not-a-uuid";
		final var request = MessagingSettingsRequest.builder()
			.withValues(List.of(
				MessagingSettingsRequest.MessagingSettingValueRequest.builder()
					.withKey("key")
					.withValue("value")
					.withType("STRING")
					.build()))
			.build();

		// Act & Assert
		webTestClient.patch()
			.uri("/{municipalityId}/{id}", municipalityId, invalidId)
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(messagingSettingsServiceMock);
	}

	@Test
	void updateMessagingSettingWithEmptyValues() {
		// Arrange
		final var municipalityId = "2281";
		final var id = "123e4567-e89b-12d3-a456-426614174000";
		final var request = MessagingSettingsRequest.builder()
			.withValues(List.of())
			.build();

		// Act & Assert
		webTestClient.patch()
			.uri("/{municipalityId}/{id}", municipalityId, id)
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(messagingSettingsServiceMock);
	}

	@Test
	void updateMessagingSettingWithMissingRequestBody() {
		// Arrange
		final var municipalityId = "2281";
		final var id = "123e4567-e89b-12d3-a456-426614174000";

		// Act & Assert
		webTestClient.patch()
			.uri("/{municipalityId}/{id}", municipalityId, id)
			.contentType(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(messagingSettingsServiceMock);
	}

	@Test
	void deleteMessagingSettingNotFound() {
		// Arrange
		final var municipalityId = "2281";
		final var id = "a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d";

		doThrow(Problem.valueOf(NOT_FOUND, "Messaging setting not found for municipality with ID '" + municipalityId + "' and ID '" + id + "'."))
			.when(messagingSettingsServiceMock).deleteMessagingSetting(municipalityId, id);

		// Act & Assert
		webTestClient.delete()
			.uri("/{municipalityId}/{id}", municipalityId, id)
			.exchange()
			.expectStatus().isNotFound()
			.expectBody()
			.jsonPath("$.title").isEqualTo("Not Found")
			.jsonPath("$.status").isEqualTo(404);
	}

	@Test
	void deleteMessagingSettingWithInvalidMunicipalityId() {
		// Arrange
		final var invalidMunicipalityId = "invalid";
		final var id = "123e4567-e89b-12d3-a456-426614174000";

		// Act & Assert
		webTestClient.delete()
			.uri("/{municipalityId}/{id}", invalidMunicipalityId, id)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(messagingSettingsServiceMock);
	}

	@Test
	void deleteMessagingSettingWithInvalidUuid() {
		// Arrange
		final var municipalityId = "2281";
		final var invalidId = "not-a-uuid";

		// Act & Assert
		webTestClient.delete()
			.uri("/{municipalityId}/{id}", municipalityId, invalidId)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(messagingSettingsServiceMock);
	}

	@Test
	void deleteMessagingSettingKeyNotFound() {
		// Arrange
		final var municipalityId = "2281";
		final var id = "a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d";
		final var key = "non_existent_key";

		doThrow(Problem.valueOf(NOT_FOUND, "Key '" + key + "' not found in messaging setting with ID '" + id + "' for municipality '" + municipalityId + "'."))
			.when(messagingSettingsServiceMock).deleteMessagingSettingKey(municipalityId, id, key);

		// Act & Assert
		webTestClient.delete()
			.uri("/{municipalityId}/{id}/key/{key}", municipalityId, id, key)
			.exchange()
			.expectStatus().isNotFound()
			.expectBody()
			.jsonPath("$.title").isEqualTo("Not Found")
			.jsonPath("$.status").isEqualTo(404);
	}

	@Test
	void deleteMessagingSettingKeyWithInvalidMunicipalityId() {
		// Arrange
		final var invalidMunicipalityId = "invalid";
		final var id = "b82bd8ac-1507-4d9a-958d-369261eecc15";
		final var key = "department_name";

		// Act & Assert
		webTestClient.delete()
			.uri("/{municipalityId}/{id}/key/{key}", invalidMunicipalityId, id, key)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(messagingSettingsServiceMock);
	}

	@Test
	void deleteMessagingSettingKeyWithInvalidUuid() {
		// Arrange
		final var municipalityId = "2281";
		final var invalidId = "not-a-uuid";
		final var key = "department_name";

		// Act & Assert
		webTestClient.delete()
			.uri("/{municipalityId}/{id}/key/{key}", municipalityId, invalidId, key)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(messagingSettingsServiceMock);
	}

	@Test
	void deleteMessagingSettingKeyEntityNotFound() {
		// Arrange
		final var municipalityId = "2281";
		final var id = "a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d";
		final var key = "department_name";

		doThrow(Problem.valueOf(NOT_FOUND, "Messaging setting not found for municipality with ID '" + municipalityId + "' and ID '" + id + "'."))
			.when(messagingSettingsServiceMock).deleteMessagingSettingKey(municipalityId, id, key);

		// Act & Assert
		webTestClient.delete()
			.uri("/{municipalityId}/{id}/key/{key}", municipalityId, id, key)
			.exchange()
			.expectStatus().isNotFound()
			.expectBody()
			.jsonPath("$.title").isEqualTo("Not Found")
			.jsonPath("$.status").isEqualTo(404);
	}
}
