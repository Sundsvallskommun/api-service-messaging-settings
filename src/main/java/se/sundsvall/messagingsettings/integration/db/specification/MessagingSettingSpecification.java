package se.sundsvall.messagingsettings.integration.db.specification;

import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity;

import static java.util.Objects.nonNull;
import static se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity_.MUNICIPALITY_ID;

public final class MessagingSettingSpecification {

	// The constants below specify the keys in the setting_value-table that contains value-specific information
	private static final String DEPARTMENT_ID = "department_id";

	private static final SpecificationBuilder<MessagingSettingEntity> BUILDER = new SpecificationBuilder<>();

	private MessagingSettingSpecification() {
		// To prevent instantiation
	}

	/**
	 * Creates filter for matching municipality id if provided, else match all
	 *
	 * @param  municipalityId the value to compare the municipality id to
	 * @return                a specification that compares the municipality id to the given value (or match all if value is
	 *                        not provided)
	 */
	public static Specification<MessagingSettingEntity> matchesMunicipalityId(final String municipalityId) {
		return BUILDER.buildEqualFilter(MUNICIPALITY_ID, municipalityId);
	}

	/**
	 * Creates a filter for matching department name if provided, else match all
	 *
	 * @param  departmentId the value to compare department name against
	 * @return              a specification that compares department name against given value (or match all if value is not
	 *                      provided)
	 */
	public static Specification<MessagingSettingEntity> matchesDepartmentId(final String departmentId) {
		return nonNull(departmentId)
			? BUILDER.matchesSettingValueKeyIgnoreCase(DEPARTMENT_ID).and(BUILDER.matchesSettingValueValueIgnoreCase(departmentId))
			: BUILDER.matchesAll();
	}

}
