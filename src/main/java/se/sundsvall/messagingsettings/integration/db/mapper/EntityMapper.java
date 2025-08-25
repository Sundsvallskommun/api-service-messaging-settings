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
				.withSupportText(e.getSupportText())
				.withContactInformationUrl(e.getContactInformationUrl())
				.withContactInformationPhoneNumber(e.getContactInformationPhoneNumber())
				.withContactInformationEmail(e.getContactInformationEmail())
				.withContactInformationEmailName(e.getContactInformationEmailName())
				.withSmsSender(e.getSmsSender())
				.build())
			.orElse(null);
	}

	public static CallbackEmailResponse toCallbackEmail(final MessagingSettingsEntity entity) {
		return ofNullable(entity)
			.map(e -> CallbackEmailResponse.builder()
				.withCallbackEmail(e.getCallbackEmail())
				.build())
			.orElse(null);
	}

	public static PortalSettingsResponse toPortalSettings(final MessagingSettingsEntity entity) {
		return ofNullable(entity)
			.map(e -> PortalSettingsResponse.builder()
				.withMunicipalityId(e.getMunicipalityId())
				.withDepartmentName(e.getDepartmentName())
				.withSnailMailMethod(e.getSnailMailMethod())
				.build())
			.orElse(null);
	}
}
