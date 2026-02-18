package se.sundsvall.messagingsettings.integration.db.mapper;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import se.sundsvall.messagingsettings.api.model.MessagingSettings;
import se.sundsvall.messagingsettings.api.model.MessagingSettingsRequest;
import se.sundsvall.messagingsettings.api.model.MessagingSettingsRequest.MessagingSettingValueRequest;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingValueEmbeddable;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.messagingsettings.integration.db.model.enums.ValueType.BOOLEAN;
import static se.sundsvall.messagingsettings.integration.db.model.enums.ValueType.NUMERIC;
import static se.sundsvall.messagingsettings.integration.db.model.enums.ValueType.STRING;

class EntityMapperTest {

	@Test
	void toMessagingSettings() {
		final var created = OffsetDateTime.now().minusMinutes(10);
		final var id = "id";
		final var municipalityId = "municipalityId";
		final var updated = OffsetDateTime.now().plusMinutes(10);
		final var key = "key";
		final var type = BOOLEAN;
		final var value = "value";
		final var entity = MessagingSettingEntity.builder()
			.withCreated(created)
			.withId(id)
			.withMunicipalityId(municipalityId)
			.withUpdated(updated)
			.withValues(List.of(MessagingSettingValueEmbeddable.builder()
				.withKey(key)
				.withType(type)
				.withValue(value)
				.build()))
			.build();

		final var bean = EntityMapper.toMessagingSettings(entity);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getCreated()).isEqualTo(created);
		assertThat(bean.getId()).isEqualTo(id);
		assertThat(bean.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(bean.getUpdated()).isEqualTo(updated);
		assertThat(bean.getValues()).hasSize(1).satisfiesExactly(beanValue -> {
			assertThat(beanValue.getKey()).isEqualTo(key);
			assertThat(beanValue.getType()).isEqualTo(type.name());
			assertThat(beanValue.getValue()).isEqualTo(value);
		});
	}

	@Test
	void toMessagingSettingsFromNull() {
		assertThat(EntityMapper.toMessagingSettings(null)).isNull();
	}

	@Test
	void toMessagingSettingsWithEmptySource() {
		assertThat(EntityMapper.toMessagingSettings(MessagingSettingEntity.builder().build()))
			.hasAllNullFieldsOrPropertiesExcept("values")
			.extracting(MessagingSettings::getValues).asInstanceOf(InstanceOfAssertFactories.LIST).isEmpty();
	}

	@Test
	void toMessagingSettingsWithNullInValueList() {
		final var created = OffsetDateTime.now().minusMinutes(10);
		final var id = "id";
		final var municipalityId = "municipalityId";
		final var updated = OffsetDateTime.now().plusMinutes(10);
		final var key = "key";
		final var type = BOOLEAN;
		final var value = "value";
		final var values = new ArrayList<>(List.of(MessagingSettingValueEmbeddable.builder()
			.withKey(key)
			.withType(type)
			.withValue(value)
			.build()));
		values.addFirst(null); // Add to verify removal of nulls

		final var entity = MessagingSettingEntity.builder()
			.withCreated(created)
			.withId(id)
			.withMunicipalityId(municipalityId)
			.withUpdated(updated)
			.withValues(values)
			.build();

		final var bean = EntityMapper.toMessagingSettings(entity);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getCreated()).isEqualTo(created);
		assertThat(bean.getId()).isEqualTo(id);
		assertThat(bean.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(bean.getUpdated()).isEqualTo(updated);
		assertThat(bean.getValues()).hasSize(1).satisfiesExactly(beanValue -> {
			assertThat(beanValue.getKey()).isEqualTo(key);
			assertThat(beanValue.getType()).isEqualTo(type.name());
			assertThat(beanValue.getValue()).isEqualTo(value);
		});
	}

	@Test
	void toEntityFromCreateRequest() {
		// Arrange
		final var municipalityId = "2281";
		final var departmentKey = "department_name";
		final var departmentValue = "IT Department";
		final var departmentType = STRING.name();
		final var smsKey = "sms_enabled";
		final var smsValue = "true";
		final var smsType = BOOLEAN.name();
		final var request = MessagingSettingsRequest.builder()
			.withValues(List.of(
				MessagingSettingValueRequest.builder()
					.withKey(departmentKey)
					.withValue(departmentValue)
					.withType(departmentType)
					.build(),
				MessagingSettingValueRequest.builder()
					.withKey(smsKey)
					.withValue(smsValue)
					.withType(smsType)
					.build()))
			.build();

		// Act
		final var entity = EntityMapper.toEntity(municipalityId, request);

		// Assert
		assertThat(entity).isNotNull();
		assertThat(entity.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(entity.getValues()).hasSize(2);
		assertThat(entity.getValues().getFirst().getKey()).isEqualTo(departmentKey);
		assertThat(entity.getValues().getFirst().getValue()).isEqualTo(departmentValue);
		assertThat(entity.getValues().getFirst().getType()).isEqualTo(STRING);
		assertThat(entity.getValues().getLast().getKey()).isEqualTo(smsKey);
		assertThat(entity.getValues().getLast().getValue()).isEqualTo(smsValue);
		assertThat(entity.getValues().getLast().getType()).isEqualTo(BOOLEAN);
	}

	@Test
	void toEntityFromCreateRequestWithNull() {
		// Act
		final var entity = EntityMapper.toEntity("2281", null);

		// Assert
		assertThat(entity).isNull();
	}

	@Test
	void toEntityFromCreateRequestWithEmptyValues() {
		// Arrange
		final var municipalityId = "2281";
		final var request = MessagingSettingsRequest.builder()
			.withValues(List.of())
			.build();

		// Act
		final var entity = EntityMapper.toEntity(municipalityId, request);

		// Assert
		assertThat(entity).isNotNull();
		assertThat(entity.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(entity.getValues()).isEmpty();
	}

	@Test
	void updateEntityFromUpdateRequest() {
		// Arrange
		final var id = "test-id";
		final var municipalityId = "2281";
		final var departmentKey = "department_name";
		final var oldDepartmentValue = "Old Department";
		final var updatedDepartmentValue = "Updated Department";
		final var departmentType = "STRING";
		final var newKey = "new_key";
		final var newValue = "new_value";
		final var newType = NUMERIC.name();
		final var type = STRING;
		final var entity = MessagingSettingEntity.builder()
			.withId(id)
			.withMunicipalityId(municipalityId)
			.withValues(new ArrayList<>(List.of(
				MessagingSettingValueEmbeddable.builder()
					.withKey(departmentKey)
					.withValue(oldDepartmentValue)
					.withType(type)
					.build())))
			.build();

		final var request = MessagingSettingsRequest.builder()
			.withValues(List.of(
				MessagingSettingValueRequest.builder()
					.withKey(departmentKey)
					.withValue(updatedDepartmentValue)
					.withType(departmentType)
					.build(),
				MessagingSettingValueRequest.builder()
					.withKey(newKey)
					.withValue(newValue)
					.withType(newType)
					.build()))
			.build();

		// Act
		final var result = EntityMapper.updateEntity(entity, request);

		// Assert
		assertThat(result).isSameAs(entity);
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(result.getValues()).hasSize(2);
		assertThat(result.getValues().getFirst().getKey()).isEqualTo(departmentKey);
		assertThat(result.getValues().getFirst().getValue()).isEqualTo(updatedDepartmentValue);
		assertThat(result.getValues().getFirst().getType()).isEqualTo(type);
		assertThat(result.getValues().getLast().getKey()).isEqualTo(newKey);
		assertThat(result.getValues().getLast().getValue()).isEqualTo(newValue);
		assertThat(result.getValues().getLast().getType()).isEqualTo(NUMERIC);
	}

	@Test
	void updateEntityWithNullEntity() {
		// Arrange
		final var request = MessagingSettingsRequest.builder()
			.withValues(List.of())
			.build();

		// Act
		final var result = EntityMapper.updateEntity(null, request);

		// Assert
		assertThat(result).isNull();
	}

	@Test
	void updateEntityWithNullRequest() {
		// Arrange
		final var id = "test-id";
		final var municipalityId = "2281";
		final var departmentKey = "department_name";
		final var departmentName = "Old Department";
		final var type = STRING;
		final var entity = MessagingSettingEntity.builder()
			.withId(id)
			.withMunicipalityId(municipalityId)
			.withValues(new ArrayList<>(List.of(
				MessagingSettingValueEmbeddable.builder()
					.withKey(departmentKey)
					.withValue(departmentName)
					.withType(type)
					.build())))
			.build();

		// Act
		final var result = EntityMapper.updateEntity(entity, null);

		// Assert
		assertThat(result).isSameAs(entity);
		assertThat(result.getValues()).hasSize(1);
		assertThat(result.getValues().getFirst().getKey()).isEqualTo(departmentKey);
		assertThat(result.getValues().getFirst().getValue()).isEqualTo(departmentName);
		assertThat(result.getValues().getFirst().getType()).isEqualTo(type);
	}

	@Test
	void updateEntityWithEmptyValues() {
		// Arrange
		final var id = "test-id";
		final var municipalityId = "2281";
		final var departmentKey = "department_name";
		final var departmentName = "Old Department";
		final var type = STRING;
		final var entity = MessagingSettingEntity.builder()
			.withId(id)
			.withMunicipalityId(municipalityId)
			.withValues(new ArrayList<>(List.of(
				MessagingSettingValueEmbeddable.builder()
					.withKey(departmentKey)
					.withValue(departmentName)
					.withType(type)
					.build())))
			.build();

		final var request = MessagingSettingsRequest.builder()
			.withValues(List.of())
			.build();

		// Act
		final var result = EntityMapper.updateEntity(entity, request);

		// Assert
		assertThat(result).isSameAs(entity);
		assertThat(result.getValues()).hasSize(1);
		assertThat(result.getValues().getFirst().getKey()).isEqualTo(departmentKey);
		assertThat(result.getValues().getFirst().getValue()).isEqualTo(departmentName);
		assertThat(result.getValues().getFirst().getType()).isEqualTo(type);
	}
}
