package se.sundsvall.messagingsettings.service;

import static java.util.Optional.ofNullable;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.messagingsettings.integration.db.mapper.EntityMapper.toCallbackEmail;
import static se.sundsvall.messagingsettings.integration.db.mapper.EntityMapper.toPortalSettings;
import static se.sundsvall.messagingsettings.integration.db.specification.MessagingSettingSpecification.matchesDepartmentId;
import static se.sundsvall.messagingsettings.integration.db.specification.MessagingSettingSpecification.matchesMunicipalityId;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.dept44.support.Identifier;
import se.sundsvall.messagingsettings.api.model.CallbackEmailResponse;
import se.sundsvall.messagingsettings.api.model.MessagingSettings;
import se.sundsvall.messagingsettings.api.model.PortalSettingsResponse;
import se.sundsvall.messagingsettings.api.model.SenderInfoResponse;
import se.sundsvall.messagingsettings.integration.db.MessagingSettingRepository;
import se.sundsvall.messagingsettings.integration.db.mapper.EntityMapper;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity;
import se.sundsvall.messagingsettings.integration.employee.EmployeeIntegration;
import se.sundsvall.messagingsettings.service.model.DepartmentInfo;

@Service
public class MessagingSettingsService {

	static final String ERROR_MESSAGE_SENDER_INFO_NOT_FOUND = "Sender info not found for municipality with ID '%s' and department with ID '%s'.";
	static final String ERROR_MESSAGE_SENDER_INFO_NOT_FOUND_BY_NAMESPACE = "Sender info not found for municipality with ID '%s', namespace '%s' and department name '%s'.";
	static final String ERROR_MESSAGE_CALLBACK_EMAIL_NOT_FOUND = "Callback e-mail not found for municipality with ID '%s' and department with ID '%s'.";
	static final String ERROR_MESSAGE_PORTAL_SETTINGS_NOT_FOUND = "Portal settings not found for municipality with ID '%s' and department with ID '%s'.";
	static final String ERROR_MESSAGE_ORGANIZATIONAL_AFFILIATION_NOT_FOUND = "Could not determine organizational affiliation for user with login name '%s'.";
	static final String ERROR_MESSAGE_MESSAGING_SETTINGS_NOT_FOUND = "Messaging settings not found for municipality with ID '%s' and department with ID '%s'.";
	static final String ERROR_MESSAGE_USER_IDENTIFIER_NOT_FOUND = "User identifier not found.";

	private final MessagingSettingRepository messagingSettingRepository;
	private final EmployeeIntegration employeeIntegration;

	public MessagingSettingsService(final MessagingSettingRepository messagingSettingRepository,
		final EmployeeIntegration employeeIntegration) {
		this.messagingSettingRepository = messagingSettingRepository;
		this.employeeIntegration = employeeIntegration;
	}

	/**
	 * @deprecated Deprecated since 2025-10-25
	 */
	@Deprecated(since = "2.0", forRemoval = true)
	public List<SenderInfoResponse> getSenderInfo(final String municipalityId, final String departmentId, final String departmentName, final String namespace) {
		return messagingSettingRepository.findAllBySpecification(municipalityId, departmentId, departmentName, namespace).stream()
			.map(EntityMapper::toSenderInfo)
			.toList();
	}

	/**
	 * @deprecated Deprecated since 2025-10-25
	 */
	@Deprecated(since = "2.0", forRemoval = true)
	public CallbackEmailResponse getCallbackEmailByMunicipalityIdAndDepartmentId(final String municipalityId, final String departmentId) {
		final var settings = messagingSettingRepository.findAllBySpecification(municipalityId, departmentId, null, null).stream()
			.findFirst()
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_CALLBACK_EMAIL_NOT_FOUND.formatted(municipalityId, departmentId)));
		return toCallbackEmail(settings);
	}

	/**
	 * @deprecated Deprecated since 2025-10-25
	 */
	@Deprecated(since = "2.0", forRemoval = true)
	public PortalSettingsResponse getPortalSettings(final String municipalityId, final String loginName) {
		final var departmentId = employeeIntegration.getDepartmentInfo(municipalityId, loginName)
			.map(DepartmentInfo::id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_ORGANIZATIONAL_AFFILIATION_NOT_FOUND.formatted(loginName)));

		final var settings = messagingSettingRepository.findAllBySpecification(municipalityId, departmentId, null, null).stream()
			.findFirst()
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_PORTAL_SETTINGS_NOT_FOUND.formatted(municipalityId, departmentId)));

		return toPortalSettings(settings);
	}

	/**
	 * @deprecated Deprecated since 2025-10-25
	 */
	@Deprecated(since = "2.0", forRemoval = true)
	public String getUser() {
		return ofNullable(Identifier.get())
			.map(Identifier::getValue)
			.orElseThrow(() -> Problem.valueOf(BAD_REQUEST, ERROR_MESSAGE_USER_IDENTIFIER_NOT_FOUND));
	}

	/**
	 * Method returns all messaging settings that matches provided filter
	 *
	 * @param  municipalityId id of municipality to match
	 * @param  filter         optional filter to match
	 * @return                a list of MessagingSettings that matches provided filters within provided municipality
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
	 * @param  municipalityId id of municipality to match
	 * @param  identifier     identifier representing the user that has been provided in header with name x-sent-by
	 * @return                a list of MessagingSettings for the organization that matches id connected to user represented
	 *                        by the provided identifier
	 * @throws Problem        if no organization could be affilieated to provider user or if no settings was found for the
	 *                        organization
	 */
	public List<MessagingSettings> fetchMessagingSettingsForUser(final String municipalityId, final Identifier identifier) {
		final var departmentId = employeeIntegration.getDepartmentInfo(municipalityId, identifier.getValue())
			.map(DepartmentInfo::id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_ORGANIZATIONAL_AFFILIATION_NOT_FOUND.formatted(identifier.getValue())));

		if (messagingSettingRepository.count(matchesMunicipalityId(municipalityId).and(matchesDepartmentId(departmentId))) == 0) {
			throw Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_MESSAGING_SETTINGS_NOT_FOUND.formatted(municipalityId, departmentId));
		}

		return messagingSettingRepository.findAll(matchesMunicipalityId(municipalityId).and(matchesDepartmentId(departmentId))).stream()
			.map(EntityMapper::toMessagingSettings)
			.toList();
	}
}
