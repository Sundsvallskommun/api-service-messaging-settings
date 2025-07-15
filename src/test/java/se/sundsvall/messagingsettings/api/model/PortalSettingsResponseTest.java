package se.sundsvall.messagingsettings.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import se.sundsvall.messagingsettings.integration.db.entity.enums.SnailMailMethod;

class PortalSettingsResponseTest {

	@Test
	void builderAndGetters() {
		final var municipalityId = "2281";
		final var departmentName = "dept44";
		final var snailMailMethod = SnailMailMethod.EMAIL;

		final var result = PortalSettingsResponse.builder()
			.withMunicipalityId(municipalityId)
			.withDepartmentName(departmentName)
			.withSnailMailMethod(snailMailMethod)
			.build();

		assertThat(result).isInstanceOf(PortalSettingsResponse.class);
		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(result.getDepartmentName()).isEqualTo(departmentName);
		assertThat(result.getSnailMailMethod()).isEqualTo(snailMailMethod);
	}

	@Test
	void builderAndGetters_noValues() {
		final var entity = PortalSettingsResponse.builder().build();

		assertThat(entity).hasAllNullFieldsOrProperties();
	}
}
