package se.sundsvall.messagingsettings.integration.db;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import se.sundsvall.messagingsettings.integration.db.entity.MessagingSettingsEntity;
import se.sundsvall.messagingsettings.integration.db.mapper.EntityMapper;
import se.sundsvall.messagingsettings.test.annotation.UnitTest;

@UnitTest
class EntityMapperTest {

	@Test
	void toSenderInfo() {
		final var entity = MessagingSettingsEntity.builder()
			.withSupportText("Lorem ipsum dolor sit amet")
			.withContactInformationUrl("https://domain.tld/")
			.withContactInformationPhoneNumber("123-98 76 54")
			.withContactInformationEmail("test@domain.tld")
			.withSmsSender("SK")
			.build();
		final var senderInfo = EntityMapper.toSenderInfo(entity);

		assertThat(senderInfo).hasNoNullFieldsOrProperties();
		assertThat(senderInfo.getSupportText()).isEqualTo(entity.getSupportText());
		assertThat(senderInfo.getContactInformationUrl()).isEqualTo(entity.getContactInformationUrl());
		assertThat(senderInfo.getContactInformationPhoneNumber()).isEqualTo(entity.getContactInformationPhoneNumber());
		assertThat(senderInfo.getContactInformationEmail()).isEqualTo(entity.getContactInformationEmail());
		assertThat(senderInfo.getSmsSender()).isEqualTo(entity.getSmsSender());
	}

	@Test
	void toSenderInfo_withNull() {
		final var senderInfo = EntityMapper.toSenderInfo(null);

		assertThat(senderInfo).isNull();
	}

	@Test
	void toSenderInfo_withNullValues() {
		final var entity = MessagingSettingsEntity.builder().build();
		final var senderInfo = EntityMapper.toSenderInfo(entity);

		assertThat(senderInfo).hasAllNullFieldsOrProperties();
	}
}
