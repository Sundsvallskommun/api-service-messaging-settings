package se.sundsvall.messagingsettings.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SenderInfoResponseTest {

	@Test
	void builderAndGetters() {
		final var supportText = "text";
		final var contactInformationUrl = "url";
		final var contactInformationPhoneNumber = "phone number";
		final var contactInformationEmail = "email";
		final var contactInformationEmailName = "email name";
		final var smsSender = "sender name";

		final var senderInfo = SenderInfoResponse.builder()
			.withSupportText(supportText)
			.withContactInformationUrl(contactInformationUrl)
			.withContactInformationPhoneNumber(contactInformationPhoneNumber)
			.withContactInformationEmail(contactInformationEmail)
			.withContactInformationEmailName(contactInformationEmailName)
			.withSmsSender(smsSender)
			.build();

		assertThat(senderInfo).isInstanceOf(SenderInfoResponse.class);
		assertThat(senderInfo.getSupportText()).isEqualTo(supportText);
		assertThat(senderInfo.getContactInformationUrl()).isEqualTo(contactInformationUrl);
		assertThat(senderInfo.getContactInformationPhoneNumber()).isEqualTo(contactInformationPhoneNumber);
		assertThat(senderInfo.getContactInformationEmail()).isEqualTo(contactInformationEmail);
		assertThat(senderInfo.getContactInformationEmailName()).isEqualTo(contactInformationEmailName);
		assertThat(senderInfo.getSmsSender()).isEqualTo(smsSender);
		assertThat(senderInfo).hasNoNullFieldsOrProperties();
	}

	@Test
	void builderAndGetters_noValues() {
		final var entity = SenderInfoResponse.builder().build();

		assertThat(entity).hasAllNullFieldsOrProperties();
	}
}
