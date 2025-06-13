package se.sundsvall.messagingsettings.integration.db.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import se.sundsvall.messagingsettings.integration.db.entity.enums.SnailMailMethod;

class MessagingSettingsEntityTest {

	@Test
	void builderAndGetters() {
		final var id = "475dcfd4-21d5-4f1d-9aac-fbf247f889b7";
		final var municipalityId = "2281";
		final var departmentId = "SKM";
		final var departmentName = "dept";
		final var snailMailMethod = SnailMailMethod.EMAIL;
		final var callbackEmail = "no-reply@localhost.local";
		final var supportText = "Lorem ipsum dolor sit amet";
		final var contactInformationUrl = "https://domain.tld/";
		final var contactInformationPhoneNumber = "123-98 76 54";
		final var contactInformationEmail = "test@domain.tld";
		final var smsSender = "SK";
		final var created = OffsetDateTime.parse("2025-05-01T10:00:00Z");
		final var updated = OffsetDateTime.parse("2025-05-01T11:30:00Z");

		final var entity = MessagingSettingsEntity.builder()
			.withId(id)
			.withMunicipalityId(municipalityId)
			.withDepartmentId(departmentId)
			.withDepartmentName(departmentName)
			.withSnailMailMethod(snailMailMethod)
			.withCallbackEmail(callbackEmail)
			.withSupportText(supportText)
			.withContactInformationUrl(contactInformationUrl)
			.withContactInformationPhoneNumber(contactInformationPhoneNumber)
			.withContactInformationEmail(contactInformationEmail)
			.withSmsSender(smsSender)
			.withCreated(created)
			.withUpdated(updated)
			.build();

		assertThat(entity).hasNoNullFieldsOrProperties();
		assertThat(entity.getId()).isEqualTo(id);
		assertThat(entity.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(entity.getDepartmentId()).isEqualTo(departmentId);
		assertThat(entity.getDepartmentName()).isEqualTo(departmentName);
		assertThat(entity.getSnailMailMethod()).isEqualTo(snailMailMethod);
		assertThat(entity.getCallbackEmail()).isEqualTo(callbackEmail);
		assertThat(entity.getSupportText()).isEqualTo(supportText);
		assertThat(entity.getContactInformationUrl()).isEqualTo(contactInformationUrl);
		assertThat(entity.getContactInformationPhoneNumber()).isEqualTo(contactInformationPhoneNumber);
		assertThat(entity.getContactInformationEmail()).isEqualTo(contactInformationEmail);
		assertThat(entity.getSmsSender()).isEqualTo(smsSender);
		assertThat(entity.getCreated()).isEqualTo(created);
		assertThat(entity.getUpdated()).isEqualTo(updated);
	}

	@Test
	void builderAndGetters_noValues() {
		final var entity = MessagingSettingsEntity.builder().build();

		assertThat(entity).hasAllNullFieldsOrProperties();
	}
}
