package se.sundsvall.messagingsettings.integration.db;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.sundsvall.messagingsettings.integration.db.entity.MessagingSettingsEntity;

@Repository
public interface MessagingSettingsRepository extends JpaRepository<MessagingSettingsEntity, String> {

	Optional<MessagingSettingsEntity> findByMunicipalityIdAndDepartmentId(final String municipalityId, final String departmentId);

	List<MessagingSettingsEntity> findAllByMunicipalityIdAndNamespace(final String municipalityId, final String namespace);

	Optional<MessagingSettingsEntity> findByMunicipalityIdAndNamespaceAndDepartmentName(final String municipalityId, final String namespace, final String departmentName);

}
