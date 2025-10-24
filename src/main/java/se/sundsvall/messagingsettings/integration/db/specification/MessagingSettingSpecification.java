package se.sundsvall.messagingsettings.integration.db.specification;

import static java.util.Objects.nonNull;
import static se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity_.MUNICIPALITY_ID;

import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity;

public class MessagingSettingSpecification {

	// Constants below specifies the keys in the setting_value-table that contains value specific information
	private static final String DEPARTMENT_ID = "department_id";
	private static final String DEPARTMENT_NAME = "department_name";
	private static final String NAMESPACE = "namespace";

	private static final SpecificationBuilder<MessagingSettingEntity> BUILDER = new SpecificationBuilder<>();

	private MessagingSettingSpecification() {
		// To prevent instansiation
	}

	/**
	 * Creates filter for matching municipality id if provided, else match all
	 *
	 * @param  value the value to compare the municipality id to
	 * @return       a specification that compares the municipality id to the given value (or match all if value is not
	 *               provided)
	 */
	public static Specification<MessagingSettingEntity> matchesMunicipalityId(String municipalityId) {
		return BUILDER.buildEqualFilter(MUNICIPALITY_ID, municipalityId);
	}

	/**
	 * Creates filter for matching department name if provided, else match all
	 *
	 * @param  value the value to compare department name against
	 * @return       a specification that compares department name against given value (or match all if value is not
	 *               provided)
	 */
	public static Specification<MessagingSettingEntity> matchesDepartmentId(final String departmentId) {
		return nonNull(departmentId)
			? BUILDER.matchesSettingValueKeyIgnoreCase(DEPARTMENT_ID).and(BUILDER.matchesSettingValueValueIgnoreCase(departmentId))
			: BUILDER.matchesAll();
	}

	/**
	 * Creates filter for matching department name if provided, else match all
	 *
	 * @param  value the value to compare department name against
	 * @return       a specification that compares department name against given value (or match all if value is not
	 *               provided)
	 */
	public static Specification<MessagingSettingEntity> matchesDepartmentName(final String departmentName) {
		return nonNull(departmentName)
			? BUILDER.matchesSettingValueKeyIgnoreCase(DEPARTMENT_NAME).and(BUILDER.matchesSettingValueValueIgnoreCase(departmentName))
			: BUILDER.matchesAll();
	}

	/**
	 * Creates filter for matching namespace if provided, else match all
	 *
	 * @param  value the value to compare namespace against
	 * @return       a specification that compares namespace against given value (or match all if value is not provided)
	 */
	public static Specification<MessagingSettingEntity> matchesNamespace(final String namespace) {
		return nonNull(namespace)
			? BUILDER.matchesSettingValueKeyIgnoreCase(NAMESPACE).and(BUILDER.matchesSettingValueValueIgnoreCase(namespace))
			: BUILDER.matchesAll();
	}
}
