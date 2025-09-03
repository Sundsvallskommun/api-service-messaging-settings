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

class CallbackEmailResponseTest {

	@Test
	void testBean() {
		assertThat(CallbackEmailResponse.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderAndGetters() {
		final var callbackEmail = "no-reply@domain.tld";
		final var organizationNumber = "organizationNumber";

		final var result = CallbackEmailResponse.builder()
			.withCallbackEmail(callbackEmail)
			.withOrganizationNumber(organizationNumber)
			.build();

		assertThat(result).isInstanceOf(CallbackEmailResponse.class).hasNoNullFieldsOrProperties();
		assertThat(result.getCallbackEmail()).isEqualTo(callbackEmail);
		assertThat(result.getOrganizationNumber()).isEqualTo(organizationNumber);
	}

	@Test
	void builderAndGetters_noValues() {
		assertThat(CallbackEmailResponse.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new CallbackEmailResponse()).hasAllNullFieldsOrProperties();
	}
}
