package se.sundsvall.messagingsettings.api.validation.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import se.sundsvall.dept44.support.Identifier;
import se.sundsvall.messagingsettings.api.validation.ValidIdentifier;

public class ValidIdentifierConstraintValidator implements ConstraintValidator<ValidIdentifier, String> {

	@Override
	public void initialize(ValidIdentifier constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	/**
	 * Method that validates if provided x-sent-by value is valid or not. If no value is provided
	 * or if the provided value is parsed correctly (i.e creates an identifier object) the method
	 * returns true. Otherwise the method returns false.
	 */
	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		return value == null || Identifier.parse(value) != null;
	}
}
