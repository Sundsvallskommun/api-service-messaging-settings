package se.sundsvall.messagingsettings.service;

import static org.zalando.problem.Status.NOT_FOUND;

import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.messagingsettings.api.model.SenderInfoResponse;

@Service
public class MessagingSettingsService {

	static final String ERROR_MESSAGE_SENDER_INFO_NOT_FOUND = "Sender info not found for municipality with ID '%s' and department with ID '%s'.";

	public SenderInfoResponse getSenderInfoByMunicipalityIdAndDepartmentId(final String municipalityId, final String departmentId) {
		throw Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_SENDER_INFO_NOT_FOUND.formatted(municipalityId, departmentId));
	}
}
