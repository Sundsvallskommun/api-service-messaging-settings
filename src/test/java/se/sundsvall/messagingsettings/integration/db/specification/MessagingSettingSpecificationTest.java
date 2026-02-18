package se.sundsvall.messagingsettings.integration.db.specification;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity;

import static org.assertj.core.api.Assertions.assertThat;

class MessagingSettingSpecificationTest {

	@Test
	void matchesMunicipalityIdWithValue() {
		// Arrange
		final var municipalityId = "2281";

		// Act
		final var specification = MessagingSettingSpecification.matchesMunicipalityId(municipalityId);

		// Assert
		assertThat(specification).isNotNull();
	}

	@Test
	void matchesMunicipalityIdWithNullValue() {
		// Act
		final var specification = MessagingSettingSpecification.matchesMunicipalityId(null);

		// Assert
		assertThat(specification).isNotNull();
	}

	@Test
	void matchesDepartmentIdWithValue() {
		// Arrange
		final var departmentId = "123";

		// Act
		final var specification = MessagingSettingSpecification.matchesDepartmentId(departmentId);

		// Assert
		assertThat(specification).isNotNull();
	}

	@Test
	void matchesDepartmentIdWithNullValue() {
		// Act
		final var specification = MessagingSettingSpecification.matchesDepartmentId(null);

		// Assert
		assertThat(specification).isNotNull();
	}

	@Test
	void matchesMunicipalityIdAndDepartmentIdCombined() {
		// Arrange
		final var municipalityId = "2281";
		final var departmentId = "123";

		// Act
		final Specification<MessagingSettingEntity> combinedSpec = MessagingSettingSpecification.matchesMunicipalityId(municipalityId)
			.and(MessagingSettingSpecification.matchesDepartmentId(departmentId));

		// Assert
		assertThat(combinedSpec).isNotNull();
	}

	@Test
	void matchesMunicipalityIdAndDepartmentIdCombinedWithNullDepartmentId() {
		// Arrange
		final var municipalityId = "2281";

		// Act
		final Specification<MessagingSettingEntity> combinedSpec = MessagingSettingSpecification.matchesMunicipalityId(municipalityId)
			.and(MessagingSettingSpecification.matchesDepartmentId(null));

		// Assert
		assertThat(combinedSpec).isNotNull();
	}
}
