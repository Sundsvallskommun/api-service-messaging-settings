package se.sundsvall.messagingsettings.api;

import static java.util.Optional.ofNullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.messagingsettings.Application;
import se.sundsvall.messagingsettings.api.model.CallbackEmailResponse;
import se.sundsvall.messagingsettings.api.model.PortalSettingsResponse;
import se.sundsvall.messagingsettings.api.model.SenderInfoResponse;
import se.sundsvall.messagingsettings.enums.SnailMailMethod;
import se.sundsvall.messagingsettings.service.MessagingSettingsService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class MessagingSettingsDeprecatedResource20251025Test {

	@MockitoBean
	private MessagingSettingsService mockMessagingSettingsService;

	@Autowired
	private WebTestClient webTestClient;

	private static Stream<Arguments> senderInfoParameterProvider() {
		return Stream.of(
			Arguments.of(null, null, null),
			Arguments.of("departmentId", null, null),
			Arguments.of(null, "departmentName", "namespace"),
			Arguments.of(null, null, "namespace"));
	}

	@ParameterizedTest
	@MethodSource("senderInfoParameterProvider")
	void getSenderInfoReturnsOK(String departmentId, String departmentName, String namespace) {
		final var municipalityId = "2281";
		final var senderInfoResponse = SenderInfoResponse.builder()
			.withSupportText("supportText")
			.withContactInformationUrl("contactInformationUrl")
			.withContactInformationPhoneNumber("contactInformationPhoneNumber")
			.withContactInformationEmail("contactInformationEmail")
			.withOrganizationNumber("organizationNumber")
			.withSmsSender("smsSender")
			.build();

		when(mockMessagingSettingsService.getSenderInfo(municipalityId, departmentId, departmentName, namespace)).thenReturn(List.of(senderInfoResponse));

		webTestClient.get()
			.uri(uriBuilder -> {
				final var builder = uriBuilder.path("/{municipalityId}/sender-info");
				ofNullable(departmentId).ifPresent(v -> builder.queryParam("departmentId", v));
				ofNullable(departmentName).ifPresent(v -> builder.queryParam("departmentName", v));
				ofNullable(namespace).ifPresent(v -> builder.queryParam("namespace", v));
				return builder.build(Map.of("municipalityId", municipalityId));
			})
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(SenderInfoResponse.class)
			.contains(senderInfoResponse);

		verify(mockMessagingSettingsService).getSenderInfo(municipalityId, departmentId, departmentName, namespace);
		verifyNoMoreInteractions(mockMessagingSettingsService);
	}

	@Test
	void getCallbackEmail() {
		final var municipalityId = "2281";
		final var departmentId = "123";
		final var callbackEmailResponse = CallbackEmailResponse.builder()
			.withCallbackEmail("callbackEmail")
			.withOrganizationNumber("organizationNumber")
			.build();

		when(mockMessagingSettingsService.getCallbackEmailByMunicipalityIdAndDepartmentId(municipalityId, departmentId)).thenReturn(callbackEmailResponse);

		webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/{departmentId}/callback-email").build(Map.of("municipalityId", municipalityId, "departmentId", departmentId)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(CallbackEmailResponse.class)
			.isEqualTo(callbackEmailResponse);

		verify(mockMessagingSettingsService).getCallbackEmailByMunicipalityIdAndDepartmentId(municipalityId, departmentId);
		verifyNoMoreInteractions(mockMessagingSettingsService);
	}

	@Test
	void getPortalSettings() {
		final var municipalityId = "2281";
		final var loginName = "loginName";
		final var portalSettingsResponse = PortalSettingsResponse.builder()
			.withDepartmentName("departmentName")
			.withMunicipalityId("municipalityId")
			.withOrganizationNumber("organizationNumber")
			.withSnailMailMethod(SnailMailMethod.SC_ADMIN)
			.build();

		when(mockMessagingSettingsService.getUser()).thenReturn(loginName);
		when(mockMessagingSettingsService.getPortalSettings(municipalityId, loginName)).thenReturn(portalSettingsResponse);

		webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/portal-settings").build(Map.of("municipalityId", municipalityId)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(PortalSettingsResponse.class)
			.isEqualTo(portalSettingsResponse);

		verify(mockMessagingSettingsService).getUser();
		verify(mockMessagingSettingsService).getPortalSettings(municipalityId, loginName);
		verifyNoMoreInteractions(mockMessagingSettingsService);
	}
}
