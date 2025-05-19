package se.sundsvall.messagingsettings.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.messagingsettings.service.MessagingSettingsService.ERROR_MESSAGE_CALLBACK_EMAIL_NOT_FOUND;
import static se.sundsvall.messagingsettings.service.MessagingSettingsService.ERROR_MESSAGE_SENDER_INFO_NOT_FOUND;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.messagingsettings.api.model.CallbackEmailResponse;
import se.sundsvall.messagingsettings.api.model.SenderInfoResponse;
import se.sundsvall.messagingsettings.integration.db.MessagingSettingsRepository;
import se.sundsvall.messagingsettings.integration.db.entity.MessagingSettingsEntity;
import se.sundsvall.messagingsettings.test.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
class MessagingSettingsServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String DEPARTMENT_ID = "dept44";

	@Mock
	private MessagingSettingsRepository mockMessagingSettingsRepository;

	@InjectMocks
	private MessagingSettingsService messagingSettingsService;

	@Test
	void getSenderInfoByMunicipalityIdAndDepartmentId() {
		when(mockMessagingSettingsRepository.findByMunicipalityIdAndDepartmentId(MUNICIPALITY_ID, DEPARTMENT_ID))
			.thenReturn(Optional.of(MessagingSettingsEntity.builder().build()));

		assertThat(messagingSettingsService.getSenderInfoByMunicipalityIdAndDepartmentId(MUNICIPALITY_ID, DEPARTMENT_ID))
			.isInstanceOf(SenderInfoResponse.class);

		verify(mockMessagingSettingsRepository).findByMunicipalityIdAndDepartmentId(MUNICIPALITY_ID, DEPARTMENT_ID);
		verifyNoMoreInteractions(mockMessagingSettingsRepository);
	}

	@Test
	void getSenderInfoByMunicipalityIdAndDepartmentId_throwsNotFoundProblem() {
		when(mockMessagingSettingsRepository.findByMunicipalityIdAndDepartmentId(MUNICIPALITY_ID, DEPARTMENT_ID))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> messagingSettingsService.getSenderInfoByMunicipalityIdAndDepartmentId(MUNICIPALITY_ID, DEPARTMENT_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessageContaining(ERROR_MESSAGE_SENDER_INFO_NOT_FOUND.formatted(MUNICIPALITY_ID, DEPARTMENT_ID));
	}

	@Test
	void getCallbackEmailByMunicipalityIdAndDepartmentId() {
		when(mockMessagingSettingsRepository.findByMunicipalityIdAndDepartmentId(MUNICIPALITY_ID, DEPARTMENT_ID))
			.thenReturn(Optional.of(MessagingSettingsEntity.builder().build()));

		assertThat(messagingSettingsService.getCallbackEmailByMunicipalityIdAndDepartmentId(MUNICIPALITY_ID, DEPARTMENT_ID))
			.isInstanceOf(CallbackEmailResponse.class);

		verify(mockMessagingSettingsRepository).findByMunicipalityIdAndDepartmentId(MUNICIPALITY_ID, DEPARTMENT_ID);
		verifyNoMoreInteractions(mockMessagingSettingsRepository);
	}

	@Test
	void getCallbackEmailByMunicipalityIdAndDepartmentId_throwsNotFoundProblem() {
		when(mockMessagingSettingsRepository.findByMunicipalityIdAndDepartmentId(MUNICIPALITY_ID, DEPARTMENT_ID))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> messagingSettingsService.getCallbackEmailByMunicipalityIdAndDepartmentId(MUNICIPALITY_ID, DEPARTMENT_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessageContaining(ERROR_MESSAGE_CALLBACK_EMAIL_NOT_FOUND.formatted(MUNICIPALITY_ID, DEPARTMENT_ID));
	}
}
