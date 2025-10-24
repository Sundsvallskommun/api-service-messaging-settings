package se.sundsvall.messagingsettings.integration.db.specification;

import static jakarta.persistence.criteria.JoinType.LEFT;
import static java.util.Objects.nonNull;
import static se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity_.VALUES;
import static se.sundsvall.messagingsettings.integration.db.model.MessagingSettingValueEmbeddable_.KEY;
import static se.sundsvall.messagingsettings.integration.db.model.MessagingSettingValueEmbeddable_.VALUE;

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
	Specification<T> buildEqualFilter(String attribute, Object value) {
		return (entity, cq, cb) -> nonNull(value) ? cb.equal(entity.get(attribute), value) : cb.and();
	}

	/**
	 * Method builds a like filter to match key/value-pair with key matching sent in value using case insensitive matching.
	 * If value is null, method returns an always-true predicate (meaning no filtering will be applied for sent in
	 * attribute)
	 *
	 * @param  value value (or null) to compare against key-attribute in the list of key/value-pairs
	 *               for the setting
	 * @return       Specification<T> matching sent in comparison
	 */
	Specification<T> matchesSettingValueKeyIgnoreCase(String value) {
		return (entity, cq, cb) -> nonNull(value) ? cb.like(cb.lower(entity.join(VALUES, LEFT).get(KEY)), value) : cb.and();
	}

	/**
	 * Method builds a like filter to match key/value-pair with value matching sent in value using case insensitive
	 * matching. If value is null, method returns an always-true predicate (meaning no filtering will be applied for sent
	 * in attribute)
	 *
	 * @param  value value (or null) to compare against key-attribute in the list of key/value-pairs
	 *               for the setting
	 * @return       Specification<T> matching sent in comparison
	 */
	Specification<T> matchesSettingValueValueIgnoreCase(String value) {
		return (entity, cq, cb) -> nonNull(value) ? cb.like(cb.lower(entity.join(VALUES, LEFT).get(VALUE)), value) : cb.and();
	}

	/**
	 * Method returns an always-true predicate
	 *
	 * @return Specification<T> matching an always-true predicate
	 */
	Specification<T> matchesAll() {
		return (entity, cq, cb) -> cb.and();
	}
}
