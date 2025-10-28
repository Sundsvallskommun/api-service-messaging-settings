package se.sundsvall.messagingsettings.api.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.messagingsettings.api.validation.ValidIdentifier;

@ExtendWith(MockitoExtension.class)
class ValidIdentifierConstraintValidatorTest {

	@Mock
	private ValidIdentifier mockAnnotation;

	@Mock
	private ConstraintValidatorContext context;

	@Mock
	private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

	@InjectMocks
	private ValidIdentifierConstraintValidator validator;

	@BeforeEach
	void beforeEach() {
		validator.initialize(mockAnnotation);
	}

	@AfterEach
	void afterEach() {
		verifyNoMoreInteractions(mockAnnotation, context, violationBuilder);
	}

	@Test
	void nullValue() {
		final var valid = validator.isValid(null, context);

		assertThat(valid).isTrue(); // Null value is be restricted by @notNull annotation
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"joe01doe",
		"type=adAccount",
		"tape=adAccount; joe_doe",
		"type=adAccount; joe_doe; bogus",
		";",
		" ",
	})
	void invalidIdentifiers(final String headerValue) {
		final var valid = validator.isValid(headerValue, context);

		assertThat(valid).isFalse();
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"type=custom; joe01doe",
		"type=adAccount; joe_doe",
		" type = adAccount ; joe_doe ",
		"joe01doe; type=partyId",
		"joe_doe; type=adAccount",
	})
	void validIdentifiers(final String headerValue) {
		final var valid = validator.isValid(headerValue, context);

		assertThat(valid).isTrue();
	}

}
