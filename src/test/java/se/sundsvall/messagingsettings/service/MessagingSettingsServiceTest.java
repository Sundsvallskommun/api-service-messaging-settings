package se.sundsvall.messagingsettings.service;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.messagingsettings.integration.db.model.enums.ValueType.STRING;
import static se.sundsvall.messagingsettings.integration.db.specification.MessagingSettingSpecification.matchesDepartmentId;
import static se.sundsvall.messagingsettings.integration.db.specification.MessagingSettingSpecification.matchesMunicipalityId;
import static se.sundsvall.messagingsettings.service.MessagingSettingsService.ERROR_MESSAGE_CALLBACK_EMAIL_NOT_FOUND;
import static se.sundsvall.messagingsettings.service.MessagingSettingsService.ERROR_MESSAGE_ORGANIZATIONAL_AFFILIATION_NOT_FOUND;
import static se.sundsvall.messagingsettings.service.MessagingSettingsService.ERROR_MESSAGE_PORTAL_SETTINGS_NOT_FOUND;

import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import java.util.ArrayList;
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
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.dept44.support.Identifier;
import se.sundsvall.messagingsettings.api.model.CallbackEmailResponse;
import se.sundsvall.messagingsettings.api.model.MessagingSettingsRequest;
import se.sundsvall.messagingsettings.api.model.MessagingSettingsRequest.MessagingSettingValueRequest;
import se.sundsvall.messagingsettings.api.model.SenderInfoResponse;
import se.sundsvall.messagingsettings.integration.db.MessagingSettingRepository;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingValueEmbeddable;
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

	private static Stream<Arguments> senderInfoProvider() {
		return Stream.of(
			Arguments.of(MUNICIPALITY_ID, DEPARTMENT_ID, DEPARTMENT_NAME, NAMESPACE),
			Arguments.of(null, DEPARTMENT_ID, DEPARTMENT_NAME, NAMESPACE),
			Arguments.of(MUNICIPALITY_ID, null, DEPARTMENT_NAME, NAMESPACE),
			Arguments.of(MUNICIPALITY_ID, DEPARTMENT_ID, null, NAMESPACE),
			Arguments.of(MUNICIPALITY_ID, DEPARTMENT_ID, DEPARTMENT_NAME, null),
			Arguments.of(null, null, null, null));
	}

	@AfterEach
	void verifyNoMoreMockInteractions() {
		verifyNoMoreInteractions(mockMessagingSettingRepository, mockEmployeeIntegration);
	}

	@ParameterizedTest
	@MethodSource("senderInfoProvider")
	void getSenderInfo(final String municipalityId, final String departmentId, final String departmentName, final String namespace) {
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
		specificationCaptor.getAllValues().forEach(capture -> assertThat(capture).usingRecursiveComparison().isEqualTo(matchesMunicipalityId(MUNICIPALITY_ID).and(matchesDepartmentId("44"))));
	}

	@Test
	void fetchMessagingSettingsForUserWhenUserIsMissingDepartmentInfo() {
		final var identifier = Identifier.parse(X_SENT_BY);
		assertThatThrownBy(() -> messagingSettingsService.fetchMessagingSettingsForUser(MUNICIPALITY_ID, identifier))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessage("Not Found: Could not determine organizational affiliation for user with login name 'testuser'.");

		verify(mockEmployeeIntegration).getDepartmentInfo(MUNICIPALITY_ID, LOGIN_NAME);
	}

	@Test
	void fetchMessagingSettingsForUserWhenDepartmentIsMissingSettings() {
		when(mockEmployeeIntegration.getDepartmentInfo(MUNICIPALITY_ID, LOGIN_NAME)).thenReturn(Optional.of(new DepartmentInfo(null, "44", null)));

		final var identifier = Identifier.parse(X_SENT_BY);
		assertThatThrownBy(() -> messagingSettingsService.fetchMessagingSettingsForUser(MUNICIPALITY_ID, identifier))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessage("Not Found: Messaging settings not found for municipality with ID '2281' and department with ID '44'.");

		verify(mockEmployeeIntegration).getDepartmentInfo(MUNICIPALITY_ID, LOGIN_NAME);
		verify(mockMessagingSettingRepository).count(specificationCaptor.capture());

		assertThat(specificationCaptor.getValue()).usingRecursiveComparison().isEqualTo(matchesMunicipalityId(MUNICIPALITY_ID).and(matchesDepartmentId("44")));
	}

	@Test
	void createMessagingSetting() {
		// Arrange
		final var request = MessagingSettingsRequest.builder()
			.withValues(List.of(
				MessagingSettingValueRequest.builder()
					.withKey("department_name")
					.withValue("IT Department")
					.withType("STRING")
					.build()))
			.build();

		final var savedEntity = MessagingSettingEntity.builder()
			.withId("generated-id")
			.withMunicipalityId(MUNICIPALITY_ID)
			.withValues(List.of(
				MessagingSettingValueEmbeddable.builder()
					.withKey("department_name")
					.withValue("IT Department")
					.withType(STRING)
					.build()))
			.build();

		when(mockMessagingSettingRepository.save(ArgumentMatchers.any(MessagingSettingEntity.class))).thenReturn(savedEntity);

		// Act
		final var result = messagingSettingsService.createMessagingSetting(MUNICIPALITY_ID, request);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo("generated-id");
		assertThat(result.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(result.getValues()).hasSize(1);
		assertThat(result.getValues().getFirst().getKey()).isEqualTo("department_name");
		assertThat(result.getValues().getFirst().getValue()).isEqualTo("IT Department");
		assertThat(result.getValues().getFirst().getType()).isEqualTo("STRING");

		verify(mockMessagingSettingRepository).save(ArgumentMatchers.any(MessagingSettingEntity.class));
	}

	@Test
	void getMessagingSettingById() {
		// Arrange
		final var id = "test-id";
		final var entity = MessagingSettingEntity.builder()
			.withId(id)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withValues(List.of(
				MessagingSettingValueEmbeddable.builder()
					.withKey("department_name")
					.withValue("IT Department")
					.withType(STRING)
					.build()))
			.build();

		when(mockMessagingSettingRepository.findByIdAndMunicipalityId(id, MUNICIPALITY_ID)).thenReturn(Optional.of(entity));

		// Act
		final var result = messagingSettingsService.getMessagingSettingById(MUNICIPALITY_ID, id);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(result.getValues()).hasSize(1);

		verify(mockMessagingSettingRepository).findByIdAndMunicipalityId(id, MUNICIPALITY_ID);
	}

	@Test
	void getMessagingSettingByIdNotFound() {
		// Arrange
		final var id = "non-existent-id";
		when(mockMessagingSettingRepository.findByIdAndMunicipalityId(id, MUNICIPALITY_ID)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> messagingSettingsService.getMessagingSettingById(MUNICIPALITY_ID, id))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessage("Not Found: Messaging setting not found for municipality with ID '2281' and ID 'non-existent-id'.");

		verify(mockMessagingSettingRepository).findByIdAndMunicipalityId(id, MUNICIPALITY_ID);
	}

	@Test
	void updateMessagingSetting() {
		// Arrange
		final var id = "test-id";
		final var request = MessagingSettingsRequest.builder()
			.withValues(List.of(
				MessagingSettingValueRequest.builder()
					.withKey("department_name")
					.withValue("Updated Department")
					.withType("STRING")
					.build()))
			.build();

		final var existingEntity = MessagingSettingEntity.builder()
			.withId(id)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withValues(new ArrayList<>(List.of(
				MessagingSettingValueEmbeddable.builder()
					.withKey("department_name")
					.withValue("Old Department")
					.withType(STRING)
					.build())))
			.build();

		final var updatedEntity = MessagingSettingEntity.builder()
			.withId(id)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withValues(List.of(
				MessagingSettingValueEmbeddable.builder()
					.withKey("department_name")
					.withValue("Updated Department")
					.withType(STRING)
					.build()))
			.build();

		when(mockMessagingSettingRepository.findByIdAndMunicipalityId(id, MUNICIPALITY_ID)).thenReturn(Optional.of(existingEntity));
		when(mockMessagingSettingRepository.save(ArgumentMatchers.any(MessagingSettingEntity.class))).thenReturn(updatedEntity);

		// Act
		final var result = messagingSettingsService.updateMessagingSetting(MUNICIPALITY_ID, id, request);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(result.getValues()).hasSize(1);
		assertThat(result.getValues().getFirst().getValue()).isEqualTo("Updated Department");

		verify(mockMessagingSettingRepository).findByIdAndMunicipalityId(id, MUNICIPALITY_ID);
		verify(mockMessagingSettingRepository).save(ArgumentMatchers.any(MessagingSettingEntity.class));
	}

	@Test
	void updateMessagingSettingNotFound() {
		// Arrange
		final var id = "non-existent-id";
		final var request = MessagingSettingsRequest.builder()
			.withValues(List.of())
			.build();

		when(mockMessagingSettingRepository.findByIdAndMunicipalityId(id, MUNICIPALITY_ID)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> messagingSettingsService.updateMessagingSetting(MUNICIPALITY_ID, id, request))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessage("Not Found: Messaging setting not found for municipality with ID '2281' and ID 'non-existent-id'.");

		verify(mockMessagingSettingRepository).findByIdAndMunicipalityId(id, MUNICIPALITY_ID);
	}

	@Test
	void deleteMessagingSetting() {
		// Arrange
		final var id = "test-id";
		final var entity = MessagingSettingEntity.builder()
			.withId(id)
			.withMunicipalityId(MUNICIPALITY_ID)
			.build();

		when(mockMessagingSettingRepository.findByIdAndMunicipalityId(id, MUNICIPALITY_ID)).thenReturn(Optional.of(entity));

		// Act
		messagingSettingsService.deleteMessagingSetting(MUNICIPALITY_ID, id);

		// Assert
		verify(mockMessagingSettingRepository).findByIdAndMunicipalityId(id, MUNICIPALITY_ID);
		verify(mockMessagingSettingRepository).delete(entity);
	}

	@Test
	void deleteMessagingSettingNotFound() {
		// Arrange
		final var id = "non-existent-id";
		when(mockMessagingSettingRepository.findByIdAndMunicipalityId(id, MUNICIPALITY_ID)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> messagingSettingsService.deleteMessagingSetting(MUNICIPALITY_ID, id))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessage("Not Found: Messaging setting not found for municipality with ID '2281' and ID 'non-existent-id'.");

		verify(mockMessagingSettingRepository).findByIdAndMunicipalityId(id, MUNICIPALITY_ID);
	}

	@Test
	void deleteMessagingSettingKey() {
		// Arrange
		final var id = "test-id";
		final var keyToDelete = "department_name";
		final var remainingKey = "sms_enabled";
		final var entity = MessagingSettingEntity.builder()
			.withId(id)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withValues(new ArrayList<>(List.of(
				MessagingSettingValueEmbeddable.builder()
					.withKey(keyToDelete)
					.withValue("IT Department")
					.withType(se.sundsvall.messagingsettings.integration.db.model.enums.ValueType.STRING)
					.build(),
				MessagingSettingValueEmbeddable.builder()
					.withKey(remainingKey)
					.withValue("true")
					.withType(se.sundsvall.messagingsettings.integration.db.model.enums.ValueType.BOOLEAN)
					.build())))
			.build();

		when(mockMessagingSettingRepository.findByIdAndMunicipalityId(id, MUNICIPALITY_ID)).thenReturn(Optional.of(entity));
		when(mockMessagingSettingRepository.save(ArgumentMatchers.any(MessagingSettingEntity.class))).thenReturn(entity);

		// Act
		messagingSettingsService.deleteMessagingSettingKey(MUNICIPALITY_ID, id, keyToDelete);

		// Assert
		verify(mockMessagingSettingRepository).findByIdAndMunicipalityId(id, MUNICIPALITY_ID);
		verify(mockMessagingSettingRepository).save(ArgumentMatchers.any(MessagingSettingEntity.class));
		assertThat(entity.getValues()).hasSize(1);
		assertThat(entity.getValues().getFirst().getKey()).isEqualTo(remainingKey);
	}

	@Test
	void deleteMessagingSettingKeyNotFound() {
		// Arrange
		final var id = "test-id";
		final var keyToDelete = "non_existent_key";
		final var entity = MessagingSettingEntity.builder()
			.withId(id)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withValues(new ArrayList<>(List.of(
				MessagingSettingValueEmbeddable.builder()
					.withKey("department_name")
					.withValue("IT Department")
					.withType(se.sundsvall.messagingsettings.integration.db.model.enums.ValueType.STRING)
					.build())))
			.build();

		when(mockMessagingSettingRepository.findByIdAndMunicipalityId(id, MUNICIPALITY_ID)).thenReturn(Optional.of(entity));

		// Act & Assert
		assertThatThrownBy(() -> messagingSettingsService.deleteMessagingSettingKey(MUNICIPALITY_ID, id, keyToDelete))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessage("Not Found: Key 'non_existent_key' not found in messaging setting with ID 'test-id' for municipality '2281'.");

		verify(mockMessagingSettingRepository).findByIdAndMunicipalityId(id, MUNICIPALITY_ID);
	}

	@Test
	void deleteMessagingSettingKeyEntityNotFound() {
		// Arrange
		final var id = "non-existent-id";
		final var keyToDelete = "department_name";
		when(mockMessagingSettingRepository.findByIdAndMunicipalityId(id, MUNICIPALITY_ID)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> messagingSettingsService.deleteMessagingSettingKey(MUNICIPALITY_ID, id, keyToDelete))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessage("Not Found: Messaging setting not found for municipality with ID '2281' and ID 'non-existent-id'.");

		verify(mockMessagingSettingRepository).findByIdAndMunicipalityId(id, MUNICIPALITY_ID);
	}
}
