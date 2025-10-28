package se.sundsvall.messagingsettings.api;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.dept44.support.Identifier;
import se.sundsvall.messagingsettings.Application;
import se.sundsvall.messagingsettings.api.model.MessagingSettings;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity;
import se.sundsvall.messagingsettings.service.MessagingSettingsService;

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
	void fetchMessagingSettings(String filter) {
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
		verify(messagingSettingsServiceMock).fetchMessagingSettings(eq(municipalityId), ArgumentMatchers.<Specification<MessagingSettingEntity>>any());
	}

	@Test
	void getMessagingSettingsForUser() {
		final var municipalityId = "2281";
		final var match = MessagingSettings.builder().build();

		when(messagingSettingsServiceMock.fetchMessagingSettingsForUser(eq(municipalityId), any())).thenReturn(List.of(match));

		final var response = webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/user")
				.build(Map.of("municipalityId", "2281")))
			.header("x-sent-by", "joe01doe; type=adAccount")
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(MessagingSettings.class)
			.returnResult().getResponseBody();

		verify(messagingSettingsServiceMock).fetchMessagingSettingsForUser(eq(municipalityId), identifierCaptor.capture());

		assertThat(response).hasSize(1).containsExactly(match);
		assertThat(identifierCaptor.getValue().getValue()).isEqualTo("joe01doe");
		assertThat(identifierCaptor.getValue().getType()).isEqualTo(Identifier.Type.AD_ACCOUNT);
	}

}
