package se.sundsvall.messagingsettings.service;

import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.messagingsettings.integration.db.mapper.EntityMapper.toCallbackEmail;
import static se.sundsvall.messagingsettings.integration.db.mapper.EntityMapper.toSenderInfo;

import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.messagingsettings.api.model.CallbackEmailResponse;
import se.sundsvall.messagingsettings.api.model.SenderInfoResponse;
import se.sundsvall.messagingsettings.integration.db.MessagingSettingsRepository;

@Service
public class MessagingSettingsService {

	static final String ERROR_MESSAGE_SENDER_INFO_NOT_FOUND = "Sender info not found for municipality with ID '%s' and department with ID '%s'.";
	static final String ERROR_MESSAGE_CALLBACK_EMAIL_NOT_FOUND = "Callback e-mail not found for municipality with ID '%s' and department with ID '%s'.";

	private final MessagingSettingsRepository messagingSettingsRepository;

	public MessagingSettingsService(MessagingSettingsRepository messagingSettingsRepository) {
		this.messagingSettingsRepository = messagingSettingsRepository;
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
}
