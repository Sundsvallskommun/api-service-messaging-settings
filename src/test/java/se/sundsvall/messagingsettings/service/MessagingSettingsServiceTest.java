package se.sundsvall.messagingsettings.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;
import se.sundsvall.messagingsettings.test.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
class MessagingSettingsServiceTest {

	@InjectMocks
	private MessagingSettingsService messagingSettingsService;

	@Test
	void getSenderInfoThrowsNotFoundProblem() {
		final var municipalityId = "2281";
		final var departmentId = "SKM";

		assertThatThrownBy(() -> messagingSettingsService.getSenderInfoByMunicipalityIdAndDepartmentId(municipalityId, departmentId))
			.isInstanceOf(Problem.class)
			.hasMessage("Not Found: " + MessagingSettingsService.ERROR_MESSAGE_SENDER_INFO_NOT_FOUND.formatted(municipalityId, departmentId));
	}
}
