package se.sundsvall.messagingsettings.service;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.messagingsettings.integration.db.specification.MessagingSettingSpecification.matchesDepartmentId;
import static se.sundsvall.messagingsettings.integration.db.specification.MessagingSettingSpecification.matchesMunicipalityId;
import static se.sundsvall.messagingsettings.service.MessagingSettingsService.ERROR_MESSAGE_CALLBACK_EMAIL_NOT_FOUND;
import static se.sundsvall.messagingsettings.service.MessagingSettingsService.ERROR_MESSAGE_ORGANIZATIONAL_AFFILIATION_NOT_FOUND;
import static se.sundsvall.messagingsettings.service.MessagingSettingsService.ERROR_MESSAGE_PORTAL_SETTINGS_NOT_FOUND;

import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.dept44.support.Identifier;
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
	private static final String X_SENT_BY = LOGIN_NAME + "; type=Somevalue";

	@Mock
	private MessagingSettingRepository mockMessagingSettingRepository;

	@Mock
	private EmployeeIntegration mockEmployeeIntegration;

	@Spy
	private FilterSpecificationConverter filterSpecificationConverterSpy;

	@Captor
	private ArgumentCaptor<Specification<MessagingSettingEntity>> specificationCaptor;

	@InjectMocks
	private MessagingSettingsService messagingSettingsService;

	@AfterEach
	void verifyNoMoreMockInteractions() {
		verifyNoMoreInteractions(mockMessagingSettingRepository, mockEmployeeIntegration);
	}

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

	@Test
	void fetchMessagingSettings() {
		when(mockMessagingSettingRepository.findAll(ArgumentMatchers.<Specification<MessagingSettingEntity>>any())).thenReturn(List.of(MessagingSettingEntity.builder().build()));

		final Specification<MessagingSettingEntity> filter = filterSpecificationConverterSpy.convert("values.key: 'namespace' and values.value: 'NAMESPACE'");

		final var result = messagingSettingsService.fetchMessagingSettings(MUNICIPALITY_ID, filter);

		verify(mockMessagingSettingRepository).findAll(specificationCaptor.capture());

		assertThat(result).hasSize(1);
		assertThat(result.getFirst()).hasAllNullFieldsOrPropertiesExcept("values");
		assertThat(result.getFirst().getValues()).isEmpty();
		assertThat(specificationCaptor.getValue()).usingRecursiveComparison().isEqualTo(matchesMunicipalityId(MUNICIPALITY_ID).and(filter));
	}

	@Test
	void fetchMessagingSettingsForUser() {
		when(mockEmployeeIntegration.getDepartmentInfo(MUNICIPALITY_ID, LOGIN_NAME)).thenReturn(Optional.of(new DepartmentInfo(null, "44", null)));
		when(mockMessagingSettingRepository.count(ArgumentMatchers.<Specification<MessagingSettingEntity>>any())).thenReturn(1L);
		when(mockMessagingSettingRepository.findAll(ArgumentMatchers.<Specification<MessagingSettingEntity>>any())).thenReturn(List.of(MessagingSettingEntity.builder().build()));

		final var result = messagingSettingsService.fetchMessagingSettingsForUser(MUNICIPALITY_ID, Identifier.parse(X_SENT_BY));

		verify(mockEmployeeIntegration).getDepartmentInfo(MUNICIPALITY_ID, LOGIN_NAME);
		verify(mockMessagingSettingRepository).count(specificationCaptor.capture());
		verify(mockMessagingSettingRepository).findAll(specificationCaptor.capture());
		verifyNoMoreInteractions(mockMessagingSettingRepository);

		assertThat(result).hasSize(1);
		assertThat(result.getFirst()).hasAllNullFieldsOrPropertiesExcept("values");
		assertThat(result.getFirst().getValues()).isEmpty();

		assertThat(specificationCaptor.getAllValues()).hasSize(2);
		specificationCaptor.getAllValues().forEach(capture -> {
			assertThat(capture).usingRecursiveComparison().isEqualTo(matchesMunicipalityId(MUNICIPALITY_ID).and(matchesDepartmentId("44")));
		});
	}

	@Test
	void fetchMessagingSettingsForUserWhenUserIsMissingDepartmentInfo() {
		final var identifier = Identifier.parse(X_SENT_BY);
		final var e = assertThrows(ThrowableProblem.class, () -> messagingSettingsService.fetchMessagingSettingsForUser(MUNICIPALITY_ID, identifier));

		assertThat(e.getStatus()).isEqualTo(Status.NOT_FOUND);
		assertThat(e.getMessage()).isEqualTo("Not Found: Could not determine organizational affiliation for user with login name 'testuser'.");
		assertThat(e.getDetail()).isEqualTo("Could not determine organizational affiliation for user with login name 'testuser'.");

		verify(mockEmployeeIntegration).getDepartmentInfo(MUNICIPALITY_ID, LOGIN_NAME);
	}

	@Test
	void fetchMessagingSettingsForUserWhenDepartmentIsMissingSettings() {
		when(mockEmployeeIntegration.getDepartmentInfo(MUNICIPALITY_ID, LOGIN_NAME)).thenReturn(Optional.of(new DepartmentInfo(null, "44", null)));

		final var identifier = Identifier.parse(X_SENT_BY);
		final var e = assertThrows(ThrowableProblem.class, () -> messagingSettingsService.fetchMessagingSettingsForUser(MUNICIPALITY_ID, identifier));

		verify(mockEmployeeIntegration).getDepartmentInfo(MUNICIPALITY_ID, LOGIN_NAME);
		verify(mockMessagingSettingRepository).count(specificationCaptor.capture());

		assertThat(e.getStatus()).isEqualTo(Status.NOT_FOUND);
		assertThat(e.getMessage()).isEqualTo("Not Found: Messaging settings not found for municipality with ID '2281' and department with ID '44'.");
		assertThat(e.getDetail()).isEqualTo("Messaging settings not found for municipality with ID '2281' and department with ID '44'.");
		assertThat(specificationCaptor.getValue()).usingRecursiveComparison().isEqualTo(matchesMunicipalityId(MUNICIPALITY_ID).and(matchesDepartmentId("44")));
	}
}
