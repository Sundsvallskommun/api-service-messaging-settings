package se.sundsvall.messagingsettings.integration.db;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.sundsvall.messagingsettings.integration.db.entity.MessagingSettingsEntity;

@Repository
public interface MessagingSettingsRepository extends JpaRepository<MessagingSettingsEntity, String> {

	Optional<MessagingSettingsEntity> findByMunicipalityIdAndDepartmentId(String municipalityId, String departmentId);
}
