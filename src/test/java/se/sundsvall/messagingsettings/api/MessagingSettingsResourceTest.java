package se.sundsvall.messagingsettings.api;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.dept44.support.Identifier;
import se.sundsvall.messagingsettings.Application;
import se.sundsvall.messagingsettings.api.model.MessagingSettings;
import se.sundsvall.messagingsettings.api.model.MessagingSettingsRequest;
import se.sundsvall.messagingsettings.service.MessagingSettingsService;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class MessagingSettingsResourceTest {

	@MockitoBean
	private MessagingSettingsService messagingSettingsServiceMock;

	@Captor
	private ArgumentCaptor<Identifier> identifierCaptor;

	@Autowired
	private WebTestClient webTestClient;

	@AfterEach
	void verifyNoMoreMockInteractions() {
		verifyNoMoreInteractions(messagingSettingsServiceMock);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"values.key: 'namespace' and values.value: 'NS1'"
	})
	@NullAndEmptySource
	void fetchMessagingSettings(final String filter) {
		final var municipalityId = "2281";
		final var match = MessagingSettings.builder().build();

		when(messagingSettingsServiceMock.fetchMessagingSettings(eq(municipalityId), any())).thenReturn(List.of(match));

		final var response = webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}")
				.queryParamIfPresent("filter", ofNullable(filter))
				.build(Map.of("municipalityId", "2281")))
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(MessagingSettings.class)
			.returnResult().getResponseBody();

		assertThat(response).hasSize(1).containsExactly(match);
		verify(messagingSettingsServiceMock).fetchMessagingSettings(eq(municipalityId), any());
	}

	@Test
	void getMessagingSettingsForUser() {
		final var municipalityId = "2281";
		final var match = MessagingSettings.builder().build();

		when(messagingSettingsServiceMock.fetchMessagingSettingsForUser(eq(municipalityId), any(), any())).thenReturn(List.of(match));

		final var response = webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/user")
				.build(Map.of("municipalityId", "2281")))
			.header("x-sent-by", "joe01doe; type=adAccount")
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(MessagingSettings.class)
			.returnResult().getResponseBody();

		verify(messagingSettingsServiceMock).fetchMessagingSettingsForUser(eq(municipalityId), identifierCaptor.capture(), any());

		assertThat(response).hasSize(1).containsExactly(match);
		assertThat(identifierCaptor.getValue().getValue()).isEqualTo("joe01doe");
		assertThat(identifierCaptor.getValue().getType()).isEqualTo(Identifier.Type.AD_ACCOUNT);
	}

	@Test
	void createMessagingSetting() {
		// Arrange
		final var municipalityId = "2281";
		final var id = "b82bd8ac-1507-4d9a-958d-369261eecc15";
		final var request = MessagingSettingsRequest.builder()
			.withValues(List.of(
				MessagingSettingsRequest.MessagingSettingValueRequest.builder()
					.withKey("department_name")
					.withValue("IT Department")
					.withType("STRING")
					.build()))
			.build();

		final var createdSetting = MessagingSettings.builder()
			.withId(id)
			.withMunicipalityId(municipalityId)
			.withCreated(OffsetDateTime.now())
			.withValues(List.of(
				MessagingSettings.MessagingSettingValue.builder()
					.withKey("department_name")
					.withValue("IT Department")
					.withType("STRING")
					.build()))
			.build();

		when(messagingSettingsServiceMock.createMessagingSetting(eq(municipalityId), any(MessagingSettingsRequest.class)))
			.thenReturn(createdSetting);

		// Act & Assert
		webTestClient.post()
			.uri("/{municipalityId}", municipalityId)
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().value("Location", location -> assertThat(location).endsWith("/" + municipalityId + "/" + id));

		verify(messagingSettingsServiceMock).createMessagingSetting(eq(municipalityId), any(MessagingSettingsRequest.class));
	}

	@Test
	void getMessagingSettingById() {
		// Arrange
		final var municipalityId = "2281";
		final var id = "b82bd8ac-1507-4d9a-958d-369261eecc15";
		final var setting = MessagingSettings.builder()
			.withId(id)
			.withMunicipalityId(municipalityId)
			.withCreated(OffsetDateTime.now())
			.withValues(List.of(
				MessagingSettings.MessagingSettingValue.builder()
					.withKey("department_name")
					.withValue("IT Department")
					.withType("STRING")
					.build()))
			.build();

		when(messagingSettingsServiceMock.getMessagingSettingById(municipalityId, id))
			.thenReturn(setting);

		// Act & Assert
		final var response = webTestClient.get()
			.uri("/{municipalityId}/{id}", municipalityId, id)
			.exchange()
			.expectStatus().isOk()
			.expectBody(MessagingSettings.class)
			.returnResult().getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(id);
		assertThat(response.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(response.getValues()).hasSize(1);

		verify(messagingSettingsServiceMock).getMessagingSettingById(municipalityId, id);
	}

	@Test
	void updateMessagingSetting() {
		// Arrange
		final var municipalityId = "2281";
		final var id = "b82bd8ac-1507-4d9a-958d-369261eecc15";
		final var request = MessagingSettingsRequest.builder()
			.withValues(List.of(
				MessagingSettingsRequest.MessagingSettingValueRequest.builder()
					.withKey("department_name")
					.withValue("Updated Department")
					.withType("STRING")
					.build()))
			.build();

		final var updatedSetting = MessagingSettings.builder()
			.withId(id)
			.withMunicipalityId(municipalityId)
			.withCreated(OffsetDateTime.now().minusDays(1))
			.withUpdated(OffsetDateTime.now())
			.withValues(List.of(
				MessagingSettings.MessagingSettingValue.builder()
					.withKey("department_name")
					.withValue("Updated Department")
					.withType("STRING")
					.build()))
			.build();

		when(messagingSettingsServiceMock.updateMessagingSetting(eq(municipalityId), eq(id), any(MessagingSettingsRequest.class)))
			.thenReturn(updatedSetting);

		// Act & Assert
		final var response = webTestClient.patch()
			.uri("/{municipalityId}/{id}", municipalityId, id)
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isOk()
			.expectBody(MessagingSettings.class)
			.returnResult().getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(id);
		assertThat(response.getUpdated()).isNotNull();
		assertThat(response.getValues().getFirst().getValue()).isEqualTo("Updated Department");

		verify(messagingSettingsServiceMock).updateMessagingSetting(eq(municipalityId), eq(id), any(MessagingSettingsRequest.class));
	}

	@Test
	void deleteMessagingSetting() {
		// Arrange
		final var municipalityId = "2281";
		final var id = "b82bd8ac-1507-4d9a-958d-369261eecc15";

		// Act & Assert
		webTestClient.delete()
			.uri("/{municipalityId}/{id}", municipalityId, id)
			.exchange()
			.expectStatus().isNoContent();

		verify(messagingSettingsServiceMock).deleteMessagingSetting(municipalityId, id);
	}

	@Test
	void deleteMessagingSettingKey() {
		// Arrange
		final var municipalityId = "2281";
		final var id = "b82bd8ac-1507-4d9a-958d-369261eecc15";
		final var key = "department_name";

		// Act & Assert
		webTestClient.delete()
			.uri("/{municipalityId}/{id}/key/{key}", municipalityId, id, key)
			.exchange()
			.expectStatus().isNoContent();

		verify(messagingSettingsServiceMock).deleteMessagingSettingKey(municipalityId, id, key);
	}

}
