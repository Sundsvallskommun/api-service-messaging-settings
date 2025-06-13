package se.sundsvall.messagingsettings.service;

import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.messagingsettings.integration.db.mapper.EntityMapper.toCallbackEmail;
import static se.sundsvall.messagingsettings.integration.db.mapper.EntityMapper.toSenderInfo;

import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.messagingsettings.api.model.CallbackEmailResponse;
import se.sundsvall.messagingsettings.api.model.SenderInfoResponse;
import se.sundsvall.messagingsettings.integration.db.MessagingSettingsRepository;
import se.sundsvall.messagingsettings.integration.employee.EmployeeIntegration;
import se.sundsvall.messagingsettings.service.model.DepartmentInfo;

@Service
public class MessagingSettingsService {

	static final String ERROR_MESSAGE_SENDER_INFO_NOT_FOUND = "Sender info not found for municipality with ID '%s' and department with ID '%s'.";
	static final String ERROR_MESSAGE_CALLBACK_EMAIL_NOT_FOUND = "Callback e-mail not found for municipality with ID '%s' and department with ID '%s'.";
	static final String ERROR_MESSAGE_PORTAL_SETTINGS_NOT_FOUND = "Portal settings not found for municipality with ID '%s' department with ID '%s'.";
	static final String ERROR_MESSAGE_ORGANIZATIONAL_AFFILIATION_NOT_FOUND = "Could not determine organizational affiliation for user with loginname '%s'.";

	private final MessagingSettingsRepository messagingSettingsRepository;
	private final EmployeeIntegration employeeIntegration;

	public MessagingSettingsService(MessagingSettingsRepository messagingSettingsRepository, EmployeeIntegration employeeIntegration) {
		this.messagingSettingsRepository = messagingSettingsRepository;
		this.employeeIntegration = employeeIntegration;
	}

	public SenderInfoResponse getSenderInfoByMunicipalityIdAndDepartmentId(final String municipalityId, final String departmentId) {
		final var entity = messagingSettingsRepository.findByMunicipalityIdAndDepartmentId(municipalityId, departmentId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_SENDER_INFO_NOT_FOUND.formatted(municipalityId, departmentId)));
		return toSenderInfo(entity);
	}

	public CallbackEmailResponse getCallbackEmailByMunicipalityIdAndDepartmentId(final String municipalityId, final String departmentId) {
		final var entity = messagingSettingsRepository.findByMunicipalityIdAndDepartmentId(municipalityId, departmentId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_CALLBACK_EMAIL_NOT_FOUND.formatted(municipalityId, departmentId)));
		return toCallbackEmail(entity);
	}

	public void getPortalSettings(final String municipalityId, final String loginName) { // TODO: Change to correct responsetype when API is ready
		final var departmentInfo = employeeIntegration.getDepartmentInfo(municipalityId, loginName);

		if (departmentInfo.isEmpty()) {
			throw Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_ORGANIZATIONAL_AFFILIATION_NOT_FOUND.formatted(loginName));
		}
		// TODO: Map to response and return when API is ready
		departmentInfo // NOSONAR
			.map(DepartmentInfo::id)
			.map(id -> messagingSettingsRepository.findByMunicipalityIdAndDepartmentId(municipalityId, id))
			.get()
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_PORTAL_SETTINGS_NOT_FOUND.formatted(municipalityId, departmentInfo.get().id())));
	}
}
