package se.sundsvall.messagingsettings.integration.db;

import static se.sundsvall.messagingsettings.integration.db.specification.MessagingSettingsSpecification.distinct;
import static se.sundsvall.messagingsettings.integration.db.specification.MessagingSettingsSpecification.withDepartmentId;
import static se.sundsvall.messagingsettings.integration.db.specification.MessagingSettingsSpecification.withDepartmentName;
import static se.sundsvall.messagingsettings.integration.db.specification.MessagingSettingsSpecification.withMunicipalityId;
import static se.sundsvall.messagingsettings.integration.db.specification.MessagingSettingsSpecification.withNamespace;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import se.sundsvall.messagingsettings.integration.db.entity.MessagingSettingsEntity;

@Repository
public interface MessagingSettingsRepository extends JpaRepository<MessagingSettingsEntity, String>, JpaSpecificationExecutor<MessagingSettingsEntity> {

	default List<MessagingSettingsEntity> findAllBySpecification(final String municipalityId, final String departmentId, final String departmentName, final String namespace) {
		return findAll(Specification
			.allOf(withMunicipalityId(municipalityId)
				.and(withDepartmentId(departmentId))
				.and(withDepartmentName(departmentName))
				.and(withNamespace(namespace))
				.and(distinct())));
	}

}
