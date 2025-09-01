package se.sundsvall.messagingsettings.api;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.Problem;
import se.sundsvall.messagingsettings.Application;
import se.sundsvall.messagingsettings.api.model.SenderInfoResponse;
import se.sundsvall.messagingsettings.service.MessagingSettingsService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class MessagingSettingsResourceTest {

	@MockitoBean
	private MessagingSettingsService mockMessagingSettingsService;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getSenderInfoReturnsOK() {
		final var municipalityId = "2281";
		final var departmentId = "dep";
		final var senderInfoResponse = SenderInfoResponse.builder()
			.withSupportText("text")
			.withContactInformationUrl("url")
			.withContactInformationPhoneNumber("phone number")
			.withContactInformationEmail("email")
			.withSmsSender("sender name")
			.build();

		when(mockMessagingSettingsService.getSenderInfo(municipalityId, departmentId, null, null))
			.thenReturn(List.of(senderInfoResponse));

		webTestClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/{municipalityId}/sender-info")
				.queryParam("departmentId", departmentId)
				.build(Map.of("municipalityId", municipalityId)))
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(SenderInfoResponse.class)
			.contains(senderInfoResponse);

		verify(mockMessagingSettingsService).getSenderInfo(municipalityId, departmentId, null, null);
		verifyNoMoreInteractions(mockMessagingSettingsService);
	}

	@Test
	void getSenderInfoReturnsBadRequest() {
		final var municipalityId = "9999";
		webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/sender-info").build(Map.of("municipalityId", municipalityId)))
			.exchange()
			.expectStatus().isEqualTo(BAD_REQUEST)
			.expectBody(Problem.class);

		verifyNoInteractions(mockMessagingSettingsService);
	}
}
