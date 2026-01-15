package se.sundsvall.messagingsettings.integration.db;

import java.util.Optional;
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

}
