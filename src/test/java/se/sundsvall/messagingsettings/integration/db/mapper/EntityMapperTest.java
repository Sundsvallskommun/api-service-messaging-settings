package se.sundsvall.messagingsettings.integration.db.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.messagingsettings.integration.db.model.enums.ValueType.BOOLEAN;
import static se.sundsvall.messagingsettings.integration.db.model.enums.ValueType.NUMERIC;
import static se.sundsvall.messagingsettings.integration.db.model.enums.ValueType.STRING;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import se.sundsvall.messagingsettings.api.model.MessagingSettings;
import se.sundsvall.messagingsettings.api.model.MessagingSettingsRequest;
import se.sundsvall.messagingsettings.api.model.MessagingSettingsRequest.MessagingSettingValueRequest;
import se.sundsvall.messagingsettings.enums.SnailMailMethod;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingValueEmbeddable;

class EntityMapperTest {

	private static MessagingSettingValueEmbeddable createSettingValue(final String key, final String value) {
		return MessagingSettingValueEmbeddable.builder()
			.withKey(key)
			.withValue(value)
			.build();
	}

	@Test
	void toSenderInfo() {
		final var contactInformationUrl = "contactInformationUrl";
		final var contactInformationPhoneNumber = "contactInformationPhoneNumber";
		final var contactInformationEmail = "contactInformationEmail";
		final var contactInformationEmailName = "contactInformationEmailName";
		final var organizationNumber = "organizationNumber";
		final var smsSender = "smsSender";
		final var supportText = "supportText";
		final var folderName = "folderName";

		final var entity = MessagingSettingEntity.builder()
			.withValues(List.of(
				createSettingValue("contact_information_url", contactInformationUrl),
				createSettingValue("contact_information_phone_number", contactInformationPhoneNumber),
				createSettingValue("contact_information_email", contactInformationEmail),
				createSettingValue("contact_information_email_name", contactInformationEmailName),
				createSettingValue("organization_number", organizationNumber),
				createSettingValue("sms_sender", smsSender),
				createSettingValue("support_text", supportText),
				createSettingValue("folder_name", folderName)))
			.build();

		final var result = EntityMapper.toSenderInfo(entity);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getContactInformationEmail()).isEqualTo(contactInformationEmail);
		assertThat(result.getContactInformationEmailName()).isEqualTo(contactInformationEmailName);
		assertThat(result.getContactInformationPhoneNumber()).isEqualTo(contactInformationPhoneNumber);
		assertThat(result.getContactInformationUrl()).isEqualTo(contactInformationUrl);
		assertThat(result.getFolderName()).isEqualTo(folderName);
		assertThat(result.getOrganizationNumber()).isEqualTo(organizationNumber);
		assertThat(result.getSmsSender()).isEqualTo(smsSender);
		assertThat(result.getSupportText()).isEqualTo(supportText);
	}

	@Test
	void toSenderInfo_withNull() {
		assertThat(EntityMapper.toSenderInfo(null))
			.isNull();
	}

	@Test
	void toSenderInfo_withNullValues() {
		assertThat(EntityMapper.toSenderInfo(MessagingSettingEntity.builder().build()))
			.hasAllNullFieldsOrProperties();
	}

	@Test
	void toCallbackEmail() {
		final var callbackEmail = "callbackEmail";
		final var organizationNumber = "organizationNumber";
		final var entity = MessagingSettingEntity.builder()
			.withValues(List.of(
				createSettingValue("callback_email", callbackEmail),
				createSettingValue("organization_number", organizationNumber)))
			.build();

		final var result = EntityMapper.toCallbackEmail(entity);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getCallbackEmail()).isEqualTo(callbackEmail);
		assertThat(result.getOrganizationNumber()).isEqualTo(organizationNumber);
	}

	@Test
	void toCallbackEmail_withNull() {
		assertThat(EntityMapper.toCallbackEmail(null))
			.isNull();
	}

	@Test
	void toCallbackEmail_withNullValues() {
		assertThat(EntityMapper.toCallbackEmail(MessagingSettingEntity.builder().build()))
			.hasAllNullFieldsOrProperties();
	}

	@Test
	void toPortalSettings() {
		final var departmentName = "departmentName";
		final var municipalityId = "municipalityId";
		final var organizationNumber = "organizationNumber";
		final var rekEnabled = "true";
		final var smsEnabled = "false";
		final var snailMailMethod = SnailMailMethod.EMAIL;
		final var entity = MessagingSettingEntity.builder()
			.withMunicipalityId(municipalityId)
			.withValues(List.of(
				createSettingValue("department_name", departmentName),
				createSettingValue("organization_number", organizationNumber),
				createSettingValue("rek_enabled", rekEnabled),
				createSettingValue("sms_enabled", smsEnabled),
				createSettingValue("snail_mail_method", snailMailMethod.name())))
			.build();

		final var result = EntityMapper.toPortalSettings(entity);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getDepartmentName()).isEqualTo(departmentName);
		assertThat(result.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(result.getOrganizationNumber()).isEqualTo(organizationNumber);
		assertThat(result.getSnailMailMethod()).isEqualTo(snailMailMethod);
		assertThat(result.getRekEnabled()).isTrue();
		assertThat(result.getSmsEnabled()).isFalse();
	}

	@Test
	void toPortalSettings_withNull() {
		assertThat(EntityMapper.toPortalSettings(null))
			.isNull();
	}

	@Test
	void toPortalSettings_withNullValues() {
		assertThat(EntityMapper.toPortalSettings(MessagingSettingEntity.builder().build()))
			.hasAllNullFieldsOrProperties();
	}

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
