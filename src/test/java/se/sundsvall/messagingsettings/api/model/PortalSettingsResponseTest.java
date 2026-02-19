package se.sundsvall.messagingsettings.api.model;

import org.junit.jupiter.api.Test;
import se.sundsvall.messagingsettings.enums.SnailMailMethod;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class PortalSettingsResponseTest {

	@Test
	void testBean() {
		assertThat(PortalSettingsResponse.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderAndGetters() {
		final var departmentName = "dept44";
		final var municipalityId = "2281";
		final var organizationNumber = "organizationNumber";
		final var snailMailMethod = SnailMailMethod.EMAIL;
		final var smsEnabled = true;
		final var rekEnabled = false;

		final var result = PortalSettingsResponse.builder()
			.withDepartmentName(departmentName)
			.withMunicipalityId(municipalityId)
			.withOrganizationNumber(organizationNumber)
			.withSnailMailMethod(snailMailMethod)
			.withSmsEnabled(smsEnabled)
			.withRekEnabled(rekEnabled)
			.build();

		assertThat(result)
			.isInstanceOf(PortalSettingsResponse.class)
			.hasNoNullFieldsOrProperties()
			.extracting(
				PortalSettingsResponse::getDepartmentName,
				PortalSettingsResponse::getMunicipalityId,
				PortalSettingsResponse::getOrganizationNumber,
				PortalSettingsResponse::getSnailMailMethod,
				PortalSettingsResponse::getSmsEnabled,
				PortalSettingsResponse::getRekEnabled)
			.containsExactly(
				departmentName,
				municipalityId,
				organizationNumber,
				snailMailMethod,
				smsEnabled,
				rekEnabled);
	}

	@Test
	void builderAndGetters_noValues() {
		assertThat(PortalSettingsResponse.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new PortalSettingsResponse()).hasAllNullFieldsOrProperties();
	}
}
