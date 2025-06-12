package se.sundsvall.messagingsettings.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CallbackEmailResponseTest {

	@Test
	void builderAndGetters() {
		final var callbackEmail = "no-reply@domain.tld";

		final var result = CallbackEmailResponse.builder()
			.withCallbackEmail(callbackEmail)
			.build();

		assertThat(result).isInstanceOf(CallbackEmailResponse.class);
		assertThat(result.getCallbackEmail()).isEqualTo(callbackEmail);
	}

	@Test
	void builderAndGetters_noValues() {
		final var result = CallbackEmailResponse.builder().build();

		assertThat(result).hasAllNullFieldsOrProperties();
	}
}
