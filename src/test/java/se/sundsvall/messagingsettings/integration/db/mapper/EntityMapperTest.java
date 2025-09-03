package se.sundsvall.messagingsettings.integration.db.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import se.sundsvall.messagingsettings.enums.SnailMailMethod;
import se.sundsvall.messagingsettings.integration.db.entity.MessagingSettingsEntity;

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
		final var entity = MessagingSettingsEntity.builder()
			.withContactInformationUrl(contactInformationUrl)
			.withContactInformationPhoneNumber(contactInformationPhoneNumber)
			.withContactInformationEmail(contactInformationEmail)
			.withContactInformationEmailName(contactInformationEmailName)
			.withOrganizationNumber(organizationNumber)
			.withSmsSender(smsSender)
			.withSupportText(supportText)
			.build();

		final var result = EntityMapper.toSenderInfo(entity);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getContactInformationUrl()).isEqualTo(contactInformationUrl);
		assertThat(result.getContactInformationPhoneNumber()).isEqualTo(contactInformationPhoneNumber);
		assertThat(result.getContactInformationEmail()).isEqualTo(contactInformationEmail);
		assertThat(result.getContactInformationEmailName()).isEqualTo(contactInformationEmailName);
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
		assertThat(EntityMapper.toSenderInfo(MessagingSettingsEntity.builder().build()))
			.hasAllNullFieldsOrProperties();
	}

	@Test
	void toCallbackEmail() {
		final var email = "email";
		final var organizationNumber = "organizationNumber";
		final var entity = MessagingSettingsEntity.builder()
			.withCallbackEmail(email)
			.withOrganizationNumber(organizationNumber)
			.build();

		final var result = EntityMapper.toCallbackEmail(entity);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getCallbackEmail()).isEqualTo(email);
		assertThat(result.getOrganizationNumber()).isEqualTo(organizationNumber);
	}

	@Test
	void toCallbackEmail_withNull() {
		assertThat(EntityMapper.toCallbackEmail(null))
			.isNull();
	}

	@Test
	void toCallbackEmail_withNullValues() {
		assertThat(EntityMapper.toCallbackEmail(MessagingSettingsEntity.builder().build()))
			.hasAllNullFieldsOrProperties();
	}

	@Test
	void toPortalSettings() {
		final var departmentName = "departmentName";
		final var municipalityId = "municipalityId";
		final var organizationNumber = "organizationNumber";
		final var snailMailMethod = SnailMailMethod.EMAIL;
		final var entity = MessagingSettingsEntity.builder()
			.withDepartmentName(departmentName)
			.withMunicipalityId(municipalityId)
			.withOrganizationNumber(organizationNumber)
			.withSnailMailMethod(snailMailMethod)
			.build();

		final var result = EntityMapper.toPortalSettings(entity);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getDepartmentName()).isEqualTo(departmentName);
		assertThat(result.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(result.getOrganizationNumber()).isEqualTo(organizationNumber);
		assertThat(result.getSnailMailMethod()).isEqualTo(snailMailMethod);
	}

	@Test
	void toPortalSettings_withNull() {
		assertThat(EntityMapper.toPortalSettings(null))
			.isNull();
	}

	@Test
	void toPortalSettings_withNullValues() {
		assertThat(EntityMapper.toPortalSettings(MessagingSettingsEntity.builder().build()))
			.hasAllNullFieldsOrProperties();
	}
}
