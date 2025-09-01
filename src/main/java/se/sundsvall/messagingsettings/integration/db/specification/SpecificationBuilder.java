package se.sundsvall.messagingsettings.integration.db.specification;

import org.springframework.data.jpa.domain.Specification;

public class SpecificationBuilder<T> {

	private static final String MUNICIPALITY_ID = "municipalityId";
	private static final String DEPARTMENT_ID = "departmentId";
	private static final String DEPARTMENT_NAME = "departmentName";
	private static final String NAMESPACE = "namespace";

	/**
	 * Creates filter for matching municipality id if provided, else match all
	 *
	 * @param  value the value to compare the municipality id to
	 * @return       a specification that compares the municipality id to the given value (or match all if value is not
	 *               provided)
	 */
	public Specification<T> buildMunicipalityIdFilter(final String value) {
		return (entity, cq, cb) -> {
			if (value == null) {
				return cb.and();
			}
			return cb.equal(entity.get(MUNICIPALITY_ID), value);
		};
	}

	/**
	 * Creates filter for matching department id if provided, else match all
	 *
	 * @param  value the value to compare department id against
	 * @return       a specification that compares department id against given value (or match all if value is not provided)
	 */
	public Specification<T> buildDepartmentIdFilter(final String value) {
		return (entity, cq, cb) -> {
			if (value == null) {
				return cb.and();
			}

			return cb.equal(entity.get(DEPARTMENT_ID), value.trim());
		};
	}

	/**
	 * Creates filter for matching department name if provided, else match all
	 *
	 * @param  value the value to compare department name against
	 * @return       a specification that compares department name against given value (or match all if value is not
	 *               provided)
	 */
	public Specification<T> buildDepartmentNameFilter(final String value) {
		return (entity, cq, cb) -> {
			if (value == null) {
				return cb.and();
			}

			return cb.equal(entity.get(DEPARTMENT_NAME), value.trim());
		};
	}

	/**
	 * Creates filter for matching namespace if provided, else match all
	 *
	 * @param  value the value to compare namespace against
	 * @return       a specification that compares namespace against given value (or match all if value is not provided)
	 */
	public Specification<T> buildNamespaceFilter(final String value) {
		return (entity, cq, cb) -> {
			if (value == null) {
				return cb.and();
			}

			return cb.equal(entity.get(NAMESPACE), value.trim());
		};
	}

	/**
	 * Creates a distinct specification, to avoid any duplicates in the result.
	 *
	 * @return a specification that makes the result distinct
	 */
	public Specification<T> distinct() {
		return (entity, cq, cb) -> {
			cq.distinct(true);
			return null;
		};
	}

}
