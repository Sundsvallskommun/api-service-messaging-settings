package se.sundsvall.messagingsettings.integration.db.mapper;

import se.sundsvall.messagingsettings.api.model.SenderInfoResponse;
import se.sundsvall.messagingsettings.integration.db.entity.MessagingSettingsEntity;

public class EntityMapper {

	private EntityMapper() {}

	public static SenderInfoResponse toSenderInfo(MessagingSettingsEntity entity) {
		return SenderInfoResponse.builder()
			.withSupportText(entity.getSupportText())
			.withContactInformationUrl(entity.getContactInformationUrl())
			.withContactInformationPhoneNumber(entity.getContactInformationPhoneNumber())
			.withContactInformationEmail(entity.getContactInformationEmail())
			.withSmsSender(entity.getSmsSender())
			.build();
	}
}
