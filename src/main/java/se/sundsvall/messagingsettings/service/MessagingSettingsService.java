package se.sundsvall.messagingsettings.service;

import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.messagingsettings.integration.db.mapper.EntityMapper.toEntity;
import static se.sundsvall.messagingsettings.integration.db.mapper.EntityMapper.updateEntity;
import static se.sundsvall.messagingsettings.integration.db.specification.MessagingSettingSpecification.matchesDepartmentId;
import static se.sundsvall.messagingsettings.integration.db.specification.MessagingSettingSpecification.matchesMunicipalityId;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.dept44.support.Identifier;
import se.sundsvall.messagingsettings.api.model.MessagingSettings;
import se.sundsvall.messagingsettings.api.model.MessagingSettingsRequest;
import se.sundsvall.messagingsettings.integration.db.MessagingSettingRepository;
import se.sundsvall.messagingsettings.integration.db.mapper.EntityMapper;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity;
import se.sundsvall.messagingsettings.integration.employee.EmployeeIntegration;
import se.sundsvall.messagingsettings.service.model.DepartmentInfo;

@Service
public class MessagingSettingsService {

	static final String ERROR_MESSAGE_ORGANIZATIONAL_AFFILIATION_NOT_FOUND = "Could not determine organizational affiliation for user with login name '%s'.";
	static final String ERROR_MESSAGE_MESSAGING_SETTINGS_NOT_FOUND = "Messaging settings not found for municipality with ID '%s' and department with ID '%s'.";
	static final String ERROR_MESSAGE_MESSAGING_SETTING_NOT_FOUND_BY_ID = "Messaging setting not found for municipality with ID '%s' and ID '%s'.";

	private final MessagingSettingRepository messagingSettingRepository;
	private final EmployeeIntegration employeeIntegration;

	public MessagingSettingsService(final MessagingSettingRepository messagingSettingRepository,
		final EmployeeIntegration employeeIntegration) {
		this.messagingSettingRepository = messagingSettingRepository;
		this.employeeIntegration = employeeIntegration;
	}

	/**
	 * Method returns all messaging settings that match the provided filter
	 *
	 * @param  municipalityId id of municipality to match
	 * @param  filter         optional filter to match
	 * @return                a list of MessagingSettings that matches provided filters within the provided municipality
	 */
	public List<MessagingSettings> fetchMessagingSettings(final String municipalityId, final Specification<MessagingSettingEntity> filter) {
		return messagingSettingRepository.findAll(matchesMunicipalityId(municipalityId).and(filter)).stream()
			.map(EntityMapper::toMessagingSettings)
			.toList();
	}

	/**
	 * Method returns messaging settings that matches the organization affiliated to the user represented by the provided
	 * identifier
	 *
	 * @param  municipalityId   id of municipality to match
	 * @param  identifier       identifier representing the user that has been provided in header with name x-sent-by
	 * @return                  a list of MessagingSettings for the organization that matches id connected to the user
	 *                          represented by the provided identifier
	 * @throws ThrowableProblem if no organization could be affiliated to the provider user, or if no settings were found
	 *                          for the organization
	 */
	public List<MessagingSettings> fetchMessagingSettingsForUser(final String municipalityId, final Identifier identifier, final Specification<MessagingSettingEntity> filter) {
		final var departmentId = employeeIntegration.getDepartmentInfo(municipalityId, identifier.getValue())
			.map(DepartmentInfo::id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_ORGANIZATIONAL_AFFILIATION_NOT_FOUND.formatted(identifier.getValue())));

		if (messagingSettingRepository.count(matchesMunicipalityId(municipalityId).and(filter).and(matchesDepartmentId(departmentId))) == 0) {
			throw Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_MESSAGING_SETTINGS_NOT_FOUND.formatted(municipalityId, departmentId));
		}

		return messagingSettingRepository.findAll(matchesMunicipalityId(municipalityId).and(filter).and(matchesDepartmentId(departmentId))).stream()
			.map(EntityMapper::toMessagingSettings)
			.toList();
	}

	/**
	 * Create a new messaging setting
	 *
	 * @param  municipalityId id of municipality
	 * @param  request        the object requested to create the setting
	 * @return                the created MessagingSettings
	 */
	public MessagingSettings createMessagingSetting(final String municipalityId, final MessagingSettingsRequest request) {
		final var savedEntity = messagingSettingRepository.save(toEntity(municipalityId, request));
		return EntityMapper.toMessagingSettings(savedEntity);
	}

	/**
	 * Get a messaging setting by ID
	 *
	 * @param  municipalityId   id of municipality
	 * @param  id               id of the messaging setting
	 * @return                  the MessagingSettings
	 * @throws ThrowableProblem if no messaging setting is found
	 */
	public MessagingSettings getMessagingSettingById(final String municipalityId, final String id) {
		final var entity = messagingSettingRepository.findByIdAndMunicipalityId(id, municipalityId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_MESSAGING_SETTING_NOT_FOUND_BY_ID.formatted(municipalityId, id)));
		return EntityMapper.toMessagingSettings(entity);
	}

	/**
	 * Update a messaging setting
	 *
	 * @param  municipalityId   id of municipality
	 * @param  id               id of the messaging setting to update
	 * @param  request          the update request
	 * @return                  the updated MessagingSettings
	 * @throws ThrowableProblem if no messaging setting is found
	 */
	public MessagingSettings updateMessagingSetting(final String municipalityId, final String id, final MessagingSettingsRequest request) {
		final var entity = messagingSettingRepository.findByIdAndMunicipalityId(id, municipalityId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_MESSAGING_SETTING_NOT_FOUND_BY_ID.formatted(municipalityId, id)));

		final var updatedEntity = updateEntity(entity, request);
		final var savedEntity = messagingSettingRepository.save(updatedEntity);
		return EntityMapper.toMessagingSettings(savedEntity);
	}

	/**
	 * Delete a messaging setting
	 *
	 * @param  municipalityId   id of municipality
	 * @param  id               id of the messaging setting to delete
	 * @throws ThrowableProblem if no messaging setting is found
	 */
	public void deleteMessagingSetting(final String municipalityId, final String id) {
		final var entity = messagingSettingRepository.findByIdAndMunicipalityId(id, municipalityId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_MESSAGING_SETTING_NOT_FOUND_BY_ID.formatted(municipalityId, id)));

		messagingSettingRepository.delete(entity);
	}

	/**
	 * Delete a specific key from a messaging setting
	 *
	 * @param  municipalityId   id of municipality
	 * @param  id               id of the messaging setting
	 * @param  key              the key to delete
	 * @throws ThrowableProblem if no messaging setting is found or key doesn't exist
	 */
	public void deleteMessagingSettingKey(final String municipalityId, final String id, final String key) {
		final var entity = messagingSettingRepository.findByIdAndMunicipalityId(id, municipalityId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_MESSAGING_SETTING_NOT_FOUND_BY_ID.formatted(municipalityId, id)));

		final var values = entity.getValues();
		final var removed = values.removeIf(value -> key.equals(value.getKey()));

		if (!removed) {
			throw Problem.valueOf(NOT_FOUND, "Key '%s' not found in messaging setting with ID '%s' for municipality '%s'.".formatted(key, id, municipalityId));
		}

		messagingSettingRepository.save(entity);
	}
}
