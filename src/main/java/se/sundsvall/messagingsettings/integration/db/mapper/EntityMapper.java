package se.sundsvall.messagingsettings.integration.db.mapper;

import static java.util.Optional.ofNullable;

import se.sundsvall.messagingsettings.api.model.CallbackEmailResponse;
import se.sundsvall.messagingsettings.api.model.PortalSettingsResponse;
import se.sundsvall.messagingsettings.api.model.SenderInfoResponse;
import se.sundsvall.messagingsettings.integration.db.entity.MessagingSettingsEntity;

public class EntityMapper {

	private EntityMapper() {}

	public static SenderInfoResponse toSenderInfo(final MessagingSettingsEntity entity) {
		return ofNullable(entity)
			.map(e -> SenderInfoResponse.builder()
				.withContactInformationUrl(e.getContactInformationUrl())
				.withContactInformationPhoneNumber(e.getContactInformationPhoneNumber())
				.withContactInformationEmail(e.getContactInformationEmail())
				.withContactInformationEmailName(e.getContactInformationEmailName())
				.withOrganizationNumber(e.getOrganizationNumber())
				.withSmsSender(e.getSmsSender())
				.withSupportText(e.getSupportText())
				.build())
			.orElse(null);
	}

	public static CallbackEmailResponse toCallbackEmail(final MessagingSettingsEntity entity) {
		return ofNullable(entity)
			.map(e -> CallbackEmailResponse.builder()
				.withCallbackEmail(e.getCallbackEmail())
				.withOrganizationNumber(e.getOrganizationNumber())
				.build())
			.orElse(null);
	}

	public static PortalSettingsResponse toPortalSettings(final MessagingSettingsEntity entity) {
		return ofNullable(entity)
			.map(messagingSettingsEntity -> PortalSettingsResponse.builder()
				.withDepartmentName(messagingSettingsEntity.getDepartmentName())
				.withMunicipalityId(messagingSettingsEntity.getMunicipalityId())
				.withOrganizationNumber(messagingSettingsEntity.getOrganizationNumber())
				.withSnailMailMethod(messagingSettingsEntity.getSnailMailMethod())
				.withRekEnabled(messagingSettingsEntity.isRekEnabled())
				.withSmsEnabled(messagingSettingsEntity.isSmsEnabled())
				.build())
			.orElse(null);
	}
}
