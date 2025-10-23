package se.sundsvall.messagingsettings.integration.db.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.messagingsettings.enums.SnailMailMethod;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingValueEmbeddable;

class EntityMapperTest {

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

	private static MessagingSettingValueEmbeddable createSettingValue(final String key, final String value) {
		return MessagingSettingValueEmbeddable.builder()
			.withKey(key)
			.withValue(value)
			.build();
	}
}
