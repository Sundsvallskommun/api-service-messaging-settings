package se.sundsvall.messagingsettings.service;

import static java.util.Optional.ofNullable;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.messagingsettings.integration.db.mapper.EntityMapper.toCallbackEmail;
import static se.sundsvall.messagingsettings.integration.db.mapper.EntityMapper.toPortalSettings;
import static se.sundsvall.messagingsettings.integration.db.mapper.EntityMapper.toSenderInfo;

import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.dept44.support.Identifier;
import se.sundsvall.messagingsettings.api.model.CallbackEmailResponse;
import se.sundsvall.messagingsettings.api.model.PortalSettingsResponse;
import se.sundsvall.messagingsettings.api.model.SenderInfoResponse;
import se.sundsvall.messagingsettings.integration.db.MessagingSettingsRepository;
import se.sundsvall.messagingsettings.integration.employee.EmployeeIntegration;
import se.sundsvall.messagingsettings.service.model.DepartmentInfo;

@Service
public class MessagingSettingsService {

	static final String ERROR_MESSAGE_SENDER_INFO_NOT_FOUND = "Sender info not found for municipality with ID '%s' and department with ID '%s'.";
	static final String ERROR_MESSAGE_CALLBACK_EMAIL_NOT_FOUND = "Callback e-mail not found for municipality with ID '%s' and department with ID '%s'.";
	static final String ERROR_MESSAGE_PORTAL_SETTINGS_NOT_FOUND = "Portal settings not found for municipality with ID '%s' and department with ID '%s'.";
	static final String ERROR_MESSAGE_ORGANIZATIONAL_AFFILIATION_NOT_FOUND = "Could not determine organizational affiliation for user with login name '%s'.";
	static final String ERROR_MESSAGE_USER_IDENTIFIER_NOT_FOUND = "User identifier not found.";

	private final MessagingSettingsRepository messagingSettingsRepository;
	private final EmployeeIntegration employeeIntegration;

	public MessagingSettingsService(MessagingSettingsRepository messagingSettingsRepository, EmployeeIntegration employeeIntegration) {
		this.messagingSettingsRepository = messagingSettingsRepository;
		this.employeeIntegration = employeeIntegration;
	}

	public SenderInfoResponse getSenderInfoByMunicipalityIdAndDepartmentId(final String municipalityId, final String departmentId) {
		var settings = messagingSettingsRepository
			.findByMunicipalityIdAndDepartmentId(municipalityId, departmentId)
			.orElseThrow(() -> Problem.valueOf(
				NOT_FOUND,
				ERROR_MESSAGE_SENDER_INFO_NOT_FOUND.formatted(municipalityId, departmentId)));
		return toSenderInfo(settings);
	}

	public CallbackEmailResponse getCallbackEmailByMunicipalityIdAndDepartmentId(final String municipalityId, final String departmentId) {
		var settings = messagingSettingsRepository
			.findByMunicipalityIdAndDepartmentId(municipalityId, departmentId)
			.orElseThrow(() -> Problem.valueOf(
				NOT_FOUND,
				ERROR_MESSAGE_CALLBACK_EMAIL_NOT_FOUND.formatted(municipalityId, departmentId)));
		return toCallbackEmail(settings);
	}

	public PortalSettingsResponse getPortalSettings(final String municipalityId, final String loginName) {
		var departmentId = employeeIntegration
			.getDepartmentInfo(municipalityId, loginName)
			.map(DepartmentInfo::id)
			.orElseThrow(() -> Problem.valueOf(
				NOT_FOUND,
				ERROR_MESSAGE_ORGANIZATIONAL_AFFILIATION_NOT_FOUND.formatted(loginName)));

		var settings = messagingSettingsRepository
			.findByMunicipalityIdAndDepartmentId(municipalityId, departmentId)
			.orElseThrow(() -> Problem.valueOf(
				NOT_FOUND,
				ERROR_MESSAGE_PORTAL_SETTINGS_NOT_FOUND.formatted(municipalityId, departmentId)));

		return toPortalSettings(settings);
	}

	public String getUser() {
		return ofNullable(Identifier.get())
			.map(Identifier::getValue)
			.orElseThrow(() -> Problem.valueOf(BAD_REQUEST, ERROR_MESSAGE_USER_IDENTIFIER_NOT_FOUND));
	}
}
