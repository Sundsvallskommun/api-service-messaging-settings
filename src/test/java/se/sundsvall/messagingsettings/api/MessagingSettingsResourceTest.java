package se.sundsvall.messagingsettings.api;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.messagingsettings.api.MessagingSettingsResource.GET_SENDER_INFO_PATH;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.Problem;
import se.sundsvall.messagingsettings.Application;
import se.sundsvall.messagingsettings.api.model.SenderInfoResponse;
import se.sundsvall.messagingsettings.service.MessagingSettingsService;
import se.sundsvall.messagingsettings.test.annotation.UnitTest;

@UnitTest
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
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

		when(mockMessagingSettingsService.getSenderInfoByMunicipalityIdAndDepartmentId(municipalityId, departmentId))
			.thenReturn(senderInfoResponse);

		webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path(GET_SENDER_INFO_PATH).build(Map.of("municipalityId", municipalityId, "departmentId", departmentId)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(SenderInfoResponse.class)
			.isEqualTo(senderInfoResponse);

		verify(mockMessagingSettingsService).getSenderInfoByMunicipalityIdAndDepartmentId(municipalityId, departmentId);
		verifyNoMoreInteractions(mockMessagingSettingsService);
	}

	@Test
	void getSenderInfoReturnsNotFound() {
		final var municipalityId = "2281";
		final var departmentId = "dep";

		when(mockMessagingSettingsService.getSenderInfoByMunicipalityIdAndDepartmentId(municipalityId, departmentId))
			.thenThrow(Problem.valueOf(NOT_FOUND));

		webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path(GET_SENDER_INFO_PATH).build(Map.of("municipalityId", municipalityId, "departmentId", departmentId)))
			.exchange()
			.expectStatus().isNotFound();

		verify(mockMessagingSettingsService).getSenderInfoByMunicipalityIdAndDepartmentId(municipalityId, departmentId);
		verifyNoMoreInteractions(mockMessagingSettingsService);
	}

	@Test
	void getSenderInfoReturnsBadRequest() {
		final var municipalityId = "9999";
		final var departmentId = "dep";

		webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path(GET_SENDER_INFO_PATH).build(Map.of("municipalityId", municipalityId, "departmentId", departmentId)))
			.exchange()
			.expectStatus().isEqualTo(BAD_REQUEST)
			.expectBody(Problem.class);

		verifyNoInteractions(mockMessagingSettingsService);
	}
}
