package se.sundsvall.messagingsettings.integration.db.specification;

import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.messagingsettings.integration.db.entity.MessagingSettingsEntity;

public class MessagingSettingsSpecification {

	private static final String MUNICIPALITY_ID = "municipalityId";
	private static final String DEPARTMENT_ID = "departmentId";
	private static final String DEPARTMENT_NAME = "departmentName";
	private static final String NAMESPACE = "namespace";

	private static final SpecificationBuilder<MessagingSettingsEntity> BUILDER = new SpecificationBuilder<>();

	private MessagingSettingsSpecification() {
		// To prevent instansiation
	}

	public static Specification<MessagingSettingsEntity> distinct() {
		return BUILDER.distinct();
	}

	/**
	 * Creates filter for matching municipality id if provided, else match all
	 *
	 * @param  value the value to compare the municipality id to
	 * @return       a specification that compares the municipality id to the given value (or match all if value is not
	 *               provided)
	 */
	public static Specification<MessagingSettingsEntity> withMunicipalityId(final String municipalityId) {
		return BUILDER.buildEqualFilter(MUNICIPALITY_ID, municipalityId);
	}

	/**
	 * Creates filter for matching department id if provided, else match all
	 *
	 * @param  value the value to compare department id against
	 * @return       a specification that compares department id against given value (or match all if value is not provided)
	 */
	public static Specification<MessagingSettingsEntity> withDepartmentId(final String departmentId) {
		return BUILDER.buildEqualFilter(DEPARTMENT_ID, departmentId);
	}

	/**
	 * Creates filter for matching department name if provided, else match all
	 *
	 * @param  value the value to compare department name against
	 * @return       a specification that compares department name against given value (or match all if value is not
	 *               provided)
	 */
	public static Specification<MessagingSettingsEntity> withDepartmentName(final String departmentName) {
		return BUILDER.buildEqualFilter(DEPARTMENT_NAME, departmentName);
	}

	/**
	 * Creates filter for matching namespace if provided, else match all
	 *
	 * @param  value the value to compare namespace against
	 * @return       a specification that compares namespace against given value (or match all if value is not provided)
	 */
	public static Specification<MessagingSettingsEntity> withNamespace(final String namespace) {
		return BUILDER.buildEqualFilter(NAMESPACE, namespace);
	}
}
