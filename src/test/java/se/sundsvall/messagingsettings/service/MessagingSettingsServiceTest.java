package se.sundsvall.messagingsettings.service;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.messagingsettings.service.MessagingSettingsService.ERROR_MESSAGE_CALLBACK_EMAIL_NOT_FOUND;
import static se.sundsvall.messagingsettings.service.MessagingSettingsService.ERROR_MESSAGE_ORGANIZATIONAL_AFFILIATION_NOT_FOUND;
import static se.sundsvall.messagingsettings.service.MessagingSettingsService.ERROR_MESSAGE_PORTAL_SETTINGS_NOT_FOUND;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.messagingsettings.api.model.CallbackEmailResponse;
import se.sundsvall.messagingsettings.api.model.SenderInfoResponse;
import se.sundsvall.messagingsettings.integration.db.MessagingSettingRepository;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity;
import se.sundsvall.messagingsettings.integration.employee.EmployeeIntegration;
import se.sundsvall.messagingsettings.service.model.DepartmentInfo;

@ExtendWith(MockitoExtension.class)
class MessagingSettingsServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "Sundsvall";
	private static final String DEPARTMENT_ID = "dept44";
	private static final String DEPARTMENT_NAME = "Department 44";
	private static final String LOGIN_NAME = "testuser";

	@Mock
	private MessagingSettingRepository mockMessagingSettingRepository;

	@Mock
	private EmployeeIntegration mockEmployeeIntegration;

	@InjectMocks
	private MessagingSettingsService messagingSettingsService;

	@ParameterizedTest
	@MethodSource("senderInfoProvider")
	void getSenderInfo(String municipalityId, String departmentId, String departmentName, String namespace) {
		when(mockMessagingSettingRepository.findAllBySpecification(municipalityId, departmentId, departmentName, namespace))
			.thenReturn(List.of(
				MessagingSettingEntity.builder().build(),
				MessagingSettingEntity.builder().build()));

		assertThat(messagingSettingsService.getSenderInfo(municipalityId, departmentId, departmentName, namespace))
			.hasSize(2)
			.allSatisfy(response -> assertThat(response).isInstanceOf(SenderInfoResponse.class));

		verify(mockMessagingSettingRepository).findAllBySpecification(municipalityId, departmentId, departmentName, namespace);
		verifyNoMoreInteractions(mockMessagingSettingRepository);
	}

	private static Stream<Arguments> senderInfoProvider() {
		return Stream.of(
			Arguments.of(MUNICIPALITY_ID, DEPARTMENT_ID, DEPARTMENT_NAME, NAMESPACE),
			Arguments.of(null, DEPARTMENT_ID, DEPARTMENT_NAME, NAMESPACE),
			Arguments.of(MUNICIPALITY_ID, null, DEPARTMENT_NAME, NAMESPACE),
			Arguments.of(MUNICIPALITY_ID, DEPARTMENT_ID, null, NAMESPACE),
			Arguments.of(MUNICIPALITY_ID, DEPARTMENT_ID, DEPARTMENT_NAME, null),
			Arguments.of(null, null, null, null));
	}

	@Test
	void getCallbackEmailByMunicipalityIdAndDepartmentId() {
		when(mockMessagingSettingRepository.findAllBySpecification(MUNICIPALITY_ID, DEPARTMENT_ID, null, null))
			.thenReturn(List.of(MessagingSettingEntity.builder().build()));

		assertThat(messagingSettingsService.getCallbackEmailByMunicipalityIdAndDepartmentId(MUNICIPALITY_ID, DEPARTMENT_ID))
			.isInstanceOf(CallbackEmailResponse.class);

		verify(mockMessagingSettingRepository).findAllBySpecification(MUNICIPALITY_ID, DEPARTMENT_ID, null, null);
		verifyNoMoreInteractions(mockMessagingSettingRepository);
	}

	@Test
	void getCallbackEmailByMunicipalityIdAndDepartmentId_throwsNotFoundProblem() {
		when(mockMessagingSettingRepository.findAllBySpecification(MUNICIPALITY_ID, DEPARTMENT_ID, null, null))
			.thenReturn(emptyList());

		assertThatThrownBy(() -> messagingSettingsService.getCallbackEmailByMunicipalityIdAndDepartmentId(MUNICIPALITY_ID, DEPARTMENT_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessageContaining(ERROR_MESSAGE_CALLBACK_EMAIL_NOT_FOUND.formatted(MUNICIPALITY_ID, DEPARTMENT_ID));
	}

	@Test
	void getPortalSettings() {
		final var departmentInfo = new DepartmentInfo("2", DEPARTMENT_ID, "dept44");

		when(mockEmployeeIntegration.getDepartmentInfo(MUNICIPALITY_ID, LOGIN_NAME))
			.thenReturn(Optional.of(departmentInfo));
		when(mockMessagingSettingRepository.findAllBySpecification(MUNICIPALITY_ID, DEPARTMENT_ID, null, null))
			.thenReturn(List.of(MessagingSettingEntity.builder().build()));

		messagingSettingsService.getPortalSettings(MUNICIPALITY_ID, LOGIN_NAME);

		verify(mockEmployeeIntegration).getDepartmentInfo(MUNICIPALITY_ID, LOGIN_NAME);
		verify(mockMessagingSettingRepository).findAllBySpecification(MUNICIPALITY_ID, DEPARTMENT_ID, null, null);
		verifyNoMoreInteractions(mockEmployeeIntegration, mockMessagingSettingRepository);
	}

	@Test
	void getPortalSettings_withNoOrganizationalAffiliation() {
		when(mockEmployeeIntegration.getDepartmentInfo(MUNICIPALITY_ID, LOGIN_NAME))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> messagingSettingsService.getPortalSettings(MUNICIPALITY_ID, LOGIN_NAME))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessageContaining(ERROR_MESSAGE_ORGANIZATIONAL_AFFILIATION_NOT_FOUND.formatted(LOGIN_NAME));

		verify(mockEmployeeIntegration).getDepartmentInfo(MUNICIPALITY_ID, LOGIN_NAME);
		verifyNoMoreInteractions(mockEmployeeIntegration, mockMessagingSettingRepository);
	}

	@Test
	void getPortalSettings_withNoPortalSettings() {
		final var departmentInfo = new DepartmentInfo("2", DEPARTMENT_ID, "dept44");

		when(mockEmployeeIntegration.getDepartmentInfo(MUNICIPALITY_ID, LOGIN_NAME))
			.thenReturn(Optional.of(departmentInfo));
		when(mockMessagingSettingRepository.findAllBySpecification(MUNICIPALITY_ID, DEPARTMENT_ID, null, null))
			.thenReturn(emptyList());

		assertThatThrownBy(() -> messagingSettingsService.getPortalSettings(MUNICIPALITY_ID, LOGIN_NAME))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessageContaining(ERROR_MESSAGE_PORTAL_SETTINGS_NOT_FOUND.formatted(MUNICIPALITY_ID, DEPARTMENT_ID));

		verify(mockEmployeeIntegration).getDepartmentInfo(MUNICIPALITY_ID, LOGIN_NAME);
		verify(mockMessagingSettingRepository).findAllBySpecification(MUNICIPALITY_ID, DEPARTMENT_ID, null, null);
		verifyNoMoreInteractions(mockEmployeeIntegration, mockMessagingSettingRepository);
	}
}
