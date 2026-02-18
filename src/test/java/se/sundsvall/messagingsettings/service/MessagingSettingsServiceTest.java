package se.sundsvall.messagingsettings.service;

import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import se.sundsvall.messagingsettings.api.model.MessagingSettingsRequest;
import se.sundsvall.messagingsettings.api.model.MessagingSettingsRequest.MessagingSettingValueRequest;
import se.sundsvall.messagingsettings.integration.db.MessagingSettingRepository;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingValueEmbeddable;
import se.sundsvall.messagingsettings.integration.employee.EmployeeIntegration;
import se.sundsvall.messagingsettings.service.model.DepartmentInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.messagingsettings.integration.db.model.enums.ValueType.STRING;
import static se.sundsvall.messagingsettings.integration.db.specification.MessagingSettingSpecification.matchesDepartmentId;
import static se.sundsvall.messagingsettings.integration.db.specification.MessagingSettingSpecification.matchesMunicipalityId;

@ExtendWith(MockitoExtension.class)
class MessagingSettingsServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String LOGIN_NAME = "testUser";
	private static final String X_SENT_BY = LOGIN_NAME + "; type=SomeValue";

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

		final Specification<MessagingSettingEntity> filter = filterSpecificationConverterSpy.convert("values.key: 'namespace' and values.value: 'NAMESPACE'");

		when(mockEmployeeIntegration.getDepartmentInfos(MUNICIPALITY_ID, LOGIN_NAME)).thenReturn(List.of(new DepartmentInfo("2", "44", "Dept")));
		when(mockMessagingSettingRepository.findAll(ArgumentMatchers.<Specification<MessagingSettingEntity>>any())).thenReturn(List.of(MessagingSettingEntity.builder().build()));

		final var result = messagingSettingsService.fetchMessagingSettingsForUser(MUNICIPALITY_ID, Identifier.parse(X_SENT_BY), filter);

		verify(mockEmployeeIntegration).getDepartmentInfos(MUNICIPALITY_ID, LOGIN_NAME);
		verify(mockMessagingSettingRepository).findAll(specificationCaptor.capture());

		assertThat(result).hasSize(1);
		assertThat(result.getFirst()).hasAllNullFieldsOrPropertiesExcept("values");
		assertThat(result.getFirst().getValues()).isEmpty();
		assertThat(specificationCaptor.getAllValues()).hasSize(1);
		assertThat(specificationCaptor.getValue()).usingRecursiveComparison().isEqualTo(matchesMunicipalityId(MUNICIPALITY_ID).and(filter).and(matchesDepartmentId("44")));
	}

	@Test
	void fetchMessagingSettingsForUserWhenUserIsMissingDepartmentInfo() {

		final Specification<MessagingSettingEntity> filter = filterSpecificationConverterSpy.convert("values.key: 'namespace' and values.value: 'NAMESPACE'");

		when(mockEmployeeIntegration.getDepartmentInfos(MUNICIPALITY_ID, LOGIN_NAME)).thenReturn(List.of());

		final var identifier = Identifier.parse(X_SENT_BY);
		assertThatThrownBy(() -> messagingSettingsService.fetchMessagingSettingsForUser(MUNICIPALITY_ID, identifier, filter))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessage("Not Found: Messaging settings not found for municipality with ID '2281' and user 'testUser'.");

		verify(mockEmployeeIntegration).getDepartmentInfos(MUNICIPALITY_ID, LOGIN_NAME);
	}

	@Test
	void fetchMessagingSettingsForUserWhenDepartmentIsMissingSettings() {

		final Specification<MessagingSettingEntity> filter = filterSpecificationConverterSpy.convert("values.key: 'namespace' and values.value: 'NAMESPACE'");

		when(mockEmployeeIntegration.getDepartmentInfos(MUNICIPALITY_ID, LOGIN_NAME)).thenReturn(List.of(new DepartmentInfo("2", "44", "Dept")));
		when(mockMessagingSettingRepository.findAll(ArgumentMatchers.<Specification<MessagingSettingEntity>>any())).thenReturn(List.of());

		final var identifier = Identifier.parse(X_SENT_BY);
		assertThatThrownBy(() -> messagingSettingsService.fetchMessagingSettingsForUser(MUNICIPALITY_ID, identifier, filter))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessage("Not Found: Messaging settings not found for municipality with ID '2281' and user 'testUser'.");

		verify(mockEmployeeIntegration).getDepartmentInfos(MUNICIPALITY_ID, LOGIN_NAME);
		verify(mockMessagingSettingRepository).findAll(specificationCaptor.capture());

		assertThat(specificationCaptor.getValue()).usingRecursiveComparison().isEqualTo(matchesMunicipalityId(MUNICIPALITY_ID).and(filter).and(matchesDepartmentId("44")));
	}

	@Test
	void fetchMessagingSettingsForUser_withHierarchicalFallback() {

		final Specification<MessagingSettingEntity> filter = filterSpecificationConverterSpy.convert("values.key: 'namespace' and values.value: 'NAMESPACE'");

		// Return 2 departments: level 2 first (no settings), then level 1 (has settings)
		when(mockEmployeeIntegration.getDepartmentInfos(MUNICIPALITY_ID, LOGIN_NAME))
			.thenReturn(List.of(new DepartmentInfo("2", "44", "Dept"), new DepartmentInfo("1", "11", "Org")));
		when(mockMessagingSettingRepository.findAll(ArgumentMatchers.<Specification<MessagingSettingEntity>>any()))
			.thenReturn(List.of()) // First call returns empty (level 2 has no settings)
			.thenReturn(List.of(MessagingSettingEntity.builder().build())); // The second call returns settings (level 1)

		final var result = messagingSettingsService.fetchMessagingSettingsForUser(MUNICIPALITY_ID, Identifier.parse(X_SENT_BY), filter);

		verify(mockEmployeeIntegration).getDepartmentInfos(MUNICIPALITY_ID, LOGIN_NAME);
		verify(mockMessagingSettingRepository, times(2)).findAll(specificationCaptor.capture());

		assertThat(result).hasSize(1);
		assertThat(specificationCaptor.getAllValues()).hasSize(2);
	}

	@Test
	void fetchMessagingSettingsForUser_returnsFirstMatchingLevel() {

		final Specification<MessagingSettingEntity> filter = filterSpecificationConverterSpy.convert("values.key: 'namespace' and values.value: 'NAMESPACE'");

		// Return 2 departments: level 2 first (has settings), then level 1
		when(mockEmployeeIntegration.getDepartmentInfos(MUNICIPALITY_ID, LOGIN_NAME))
			.thenReturn(List.of(new DepartmentInfo("2", "44", "Dept"), new DepartmentInfo("1", "11", "Org")));
		when(mockMessagingSettingRepository.findAll(ArgumentMatchers.<Specification<MessagingSettingEntity>>any()))
			.thenReturn(List.of(MessagingSettingEntity.builder().build())); // The first call returns settings

		final var result = messagingSettingsService.fetchMessagingSettingsForUser(MUNICIPALITY_ID, Identifier.parse(X_SENT_BY), filter);

		verify(mockEmployeeIntegration).getDepartmentInfos(MUNICIPALITY_ID, LOGIN_NAME);
		// findAll should only be called once since level 2 had settings
		verify(mockMessagingSettingRepository, times(1)).findAll(specificationCaptor.capture());

		assertThat(result).hasSize(1);
		assertThat(specificationCaptor.getAllValues()).hasSize(1);
	}

	@Test
	void fetchMessagingSettingsForUser_withNoSettingsAtAnyLevel() {

		final Specification<MessagingSettingEntity> filter = filterSpecificationConverterSpy.convert("values.key: 'namespace' and values.value: 'NAMESPACE'");

		// Return 2 departments but no settings at any level
		when(mockEmployeeIntegration.getDepartmentInfos(MUNICIPALITY_ID, LOGIN_NAME))
			.thenReturn(List.of(new DepartmentInfo("2", "44", "Dept"), new DepartmentInfo("1", "11", "Org")));
		when(mockMessagingSettingRepository.findAll(ArgumentMatchers.<Specification<MessagingSettingEntity>>any()))
			.thenReturn(List.of()); // Both calls return empty

		final var identifier = Identifier.parse(X_SENT_BY);
		assertThatThrownBy(() -> messagingSettingsService.fetchMessagingSettingsForUser(MUNICIPALITY_ID, identifier, filter))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessage("Not Found: Messaging settings not found for municipality with ID '2281' and user 'testUser'.");

		verify(mockEmployeeIntegration).getDepartmentInfos(MUNICIPALITY_ID, LOGIN_NAME);
		verify(mockMessagingSettingRepository, times(2)).findAll(specificationCaptor.capture());

		assertThat(specificationCaptor.getAllValues()).hasSize(2);
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
