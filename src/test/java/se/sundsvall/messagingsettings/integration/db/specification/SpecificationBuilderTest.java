package se.sundsvall.messagingsettings.integration.db.specification;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity;

class SpecificationBuilderTest {

	private final SpecificationBuilder<MessagingSettingEntity> builder = new SpecificationBuilder<>();

	@Test
	void buildEqualFilterWithValue() {
		// Arrange
		final var attribute = "testAttribute";
		final var value = "testValue";

		// Act
		final var specification = builder.buildEqualFilter(attribute, value);

		// Assert
		assertThat(specification).isNotNull();
	}

	@Test
	void buildEqualFilterWithNullValue() {
		// Arrange
		final var attribute = "testAttribute";

		// Act
		final var specification = builder.buildEqualFilter(attribute, null);

		// Assert
		assertThat(specification).isNotNull();
	}

	@Test
	void matchesSettingValueKeyIgnoreCaseWithValue() {
		// Arrange
		final var value = "testKey";

		// Act
		final var specification = builder.matchesSettingValueKeyIgnoreCase(value);

		// Assert
		assertThat(specification).isNotNull();
	}

	@Test
	void matchesSettingValueKeyIgnoreCaseWithNullValue() {
		// Act
		final var specification = builder.matchesSettingValueKeyIgnoreCase(null);

		// Assert
		assertThat(specification).isNotNull();
	}

	@Test
	void matchesSettingValueValueIgnoreCaseWithValue() {
		// Arrange
		final var value = "testValue";

		// Act
		final var specification = builder.matchesSettingValueValueIgnoreCase(value);

		// Assert
		assertThat(specification).isNotNull();
	}

	@Test
	void matchesSettingValueValueIgnoreCaseWithNullValue() {
		// Act
		final var specification = builder.matchesSettingValueValueIgnoreCase(null);

		// Assert
		assertThat(specification).isNotNull();
	}

	@Test
	void matchesAll() {
		// Act
		final var specification = builder.matchesAll();

		// Assert
		assertThat(specification).isNotNull();
	}

	@Test
	void combinedSpecificationsWithValues() {
		// Arrange
		final var keyValue = "testKey";
		final var valueValue = "testValue";

		// Act
		final var combinedSpec = builder.matchesSettingValueKeyIgnoreCase(keyValue)
			.and(builder.matchesSettingValueValueIgnoreCase(valueValue));

		// Assert
		assertThat(combinedSpec).isNotNull();
	}

	@Test
	void combinedSpecificationsWithNullValues() {
		// Act
		final var combinedSpec = builder.matchesSettingValueKeyIgnoreCase(null)
			.and(builder.matchesSettingValueValueIgnoreCase(null));

		// Assert
		assertThat(combinedSpec).isNotNull();
	}
}
