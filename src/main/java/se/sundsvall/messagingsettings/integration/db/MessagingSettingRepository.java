package se.sundsvall.messagingsettings.integration.db;

import static se.sundsvall.messagingsettings.integration.db.specification.MessagingSettingSpecification.matchesDepartmentId;
import static se.sundsvall.messagingsettings.integration.db.specification.MessagingSettingSpecification.matchesDepartmentName;
import static se.sundsvall.messagingsettings.integration.db.specification.MessagingSettingSpecification.matchesMunicipalityId;
import static se.sundsvall.messagingsettings.integration.db.specification.MessagingSettingSpecification.matchesNamespace;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity;

@Repository
public interface MessagingSettingRepository extends JpaRepository<MessagingSettingEntity, String>, JpaSpecificationExecutor<MessagingSettingEntity> {

	/**
	 * Find a messaging setting by ID and municipality ID
	 *
	 * @param  id             the messaging setting ID
	 * @param  municipalityId the municipality ID
	 * @return                an Optional containing the entity if found
	 */
	Optional<MessagingSettingEntity> findByIdAndMunicipalityId(String id, String municipalityId);

	/**
	 * @deprecated Deprecated since 2025-10-25
	 */
	@Deprecated(since = "2.0", forRemoval = true)
	default List<MessagingSettingEntity> findAllBySpecification(final String municipalityId, final String departmentId, final String departmentName, final String namespace) {
		return findAll(Specification
			.allOf(matchesMunicipalityId(municipalityId)
				.and(matchesDepartmentId(departmentId))
				.and(matchesDepartmentName(departmentName))
				.and(matchesNamespace(namespace))));
	}
}
