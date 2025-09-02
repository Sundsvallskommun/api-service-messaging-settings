package se.sundsvall.messagingsettings.integration.db.specification;

import static java.util.Objects.nonNull;

import org.springframework.data.jpa.domain.Specification;

public class SpecificationBuilder<T> {

	/**
	 * Method builds an equal filter if value is not null. If value is null, method returns
	 * an always-true predicate (meaning no filtering will be applied for sent in attribute)
	 *
	 * @param  attribute name that will be used in filter
	 * @param  value     value (or null) to compare against
	 * @return           Specification<T> matching sent in comparison
	 */
	public Specification<T> buildEqualFilter(String attribute, Object value) {
		return (entity, cq, cb) -> nonNull(value) ? cb.equal(entity.get(attribute), value) : cb.and();
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
