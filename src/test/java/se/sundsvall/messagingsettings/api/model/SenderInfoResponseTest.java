package se.sundsvall.messagingsettings.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class SenderInfoResponseTest {

	@Test
	void testBean() {
		assertThat(SenderInfoResponse.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderAndGetters() {
		final var contactInformationUrl = "url";
		final var contactInformationPhoneNumber = "phone number";
		final var contactInformationEmail = "email";
		final var contactInformationEmailName = "email name";
		final var organizationNumber = "organizationNumber";
		final var supportText = "text";
		final var smsSender = "sender name";

		final var senderInfo = SenderInfoResponse.builder()
			.withContactInformationUrl(contactInformationUrl)
			.withContactInformationPhoneNumber(contactInformationPhoneNumber)
			.withContactInformationEmail(contactInformationEmail)
			.withContactInformationEmailName(contactInformationEmailName)
			.withOrganizationNumber(organizationNumber)
			.withSmsSender(smsSender)
			.withSupportText(supportText)
			.build();

		assertThat(senderInfo).isInstanceOf(SenderInfoResponse.class).hasNoNullFieldsOrProperties();
		assertThat(senderInfo.getContactInformationUrl()).isEqualTo(contactInformationUrl);
		assertThat(senderInfo.getContactInformationPhoneNumber()).isEqualTo(contactInformationPhoneNumber);
		assertThat(senderInfo.getContactInformationEmail()).isEqualTo(contactInformationEmail);
		assertThat(senderInfo.getContactInformationEmailName()).isEqualTo(contactInformationEmailName);
		assertThat(senderInfo.getOrganizationNumber()).isEqualTo(organizationNumber);
		assertThat(senderInfo.getSmsSender()).isEqualTo(smsSender);
		assertThat(senderInfo.getSupportText()).isEqualTo(supportText);
	}

	@Test
	void builderAndGetters_noValues() {
		assertThat(SenderInfoResponse.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new SenderInfoResponse()).hasAllNullFieldsOrProperties();
	}
}
