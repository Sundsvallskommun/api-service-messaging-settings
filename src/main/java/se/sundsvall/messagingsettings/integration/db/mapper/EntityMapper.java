package se.sundsvall.messagingsettings.integration.db.mapper;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import java.util.List;
import se.sundsvall.messagingsettings.api.model.CallbackEmailResponse;
import se.sundsvall.messagingsettings.api.model.PortalSettingsResponse;
import se.sundsvall.messagingsettings.api.model.SenderInfoResponse;
import se.sundsvall.messagingsettings.enums.SnailMailMethod;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingValueEmbeddable;

public class EntityMapper {

	private EntityMapper() {}

	public static SenderInfoResponse toSenderInfo(final MessagingSettingEntity nullableSettingEntity) {
		return ofNullable(nullableSettingEntity)
			.map(settingEntity -> SenderInfoResponse.builder()
				.withContactInformationUrl(fetchSettingValue(settingEntity.getValues(), "contact_information_url"))
				.withContactInformationPhoneNumber(fetchSettingValue(settingEntity.getValues(), "contact_information_phone_number"))
				.withContactInformationEmail(fetchSettingValue(settingEntity.getValues(), "contact_information_email"))
				.withContactInformationEmailName(fetchSettingValue(settingEntity.getValues(), "contact_information_email_name"))
				.withOrganizationNumber(fetchSettingValue(settingEntity.getValues(), "organization_number"))
				.withSmsSender(fetchSettingValue(settingEntity.getValues(), "sms_sender"))
				.withSupportText(fetchSettingValue(settingEntity.getValues(), "support_text"))
				.withFolderName(fetchSettingValue(settingEntity.getValues(), "folder_name"))
				.build())
			.orElse(null);
	}

	public static CallbackEmailResponse toCallbackEmail(final MessagingSettingEntity nullableSettingEntity) {
		return ofNullable(nullableSettingEntity)
			.map(settingEntity -> CallbackEmailResponse.builder()
				.withCallbackEmail(fetchSettingValue(settingEntity.getValues(), "callback_email"))
				.withOrganizationNumber(fetchSettingValue(settingEntity.getValues(), "organization_number"))
				.build())
			.orElse(null);
	}

	public static PortalSettingsResponse toPortalSettings(final MessagingSettingEntity nullableSettingEntity) {
		return ofNullable(nullableSettingEntity)
			.map(settingEntity -> PortalSettingsResponse.builder()
				.withDepartmentName(fetchSettingValue(settingEntity.getValues(), "department_name"))
				.withMunicipalityId(settingEntity.getMunicipalityId())
				.withOrganizationNumber(fetchSettingValue(settingEntity.getValues(), "organization_number"))
				.withSnailMailMethod(ofNullable(fetchSettingValue(settingEntity.getValues(), "snail_mail_method")).map(SnailMailMethod::valueOf).orElse(null))
				.withRekEnabled(ofNullable(fetchSettingValue(settingEntity.getValues(), "rek_enabled")).map(Boolean::valueOf).orElse(null))
				.withSmsEnabled(ofNullable(fetchSettingValue(settingEntity.getValues(), "sms_enabled")).map(Boolean::valueOf).orElse(null))
				.build())
			.orElse(null);
	}

	private static String fetchSettingValue(List<MessagingSettingValueEmbeddable> values, String nullableKey) {
		return ofNullable(nullableKey)
			.map(key -> ofNullable(values)
				.orElse(emptyList()).stream()
				.filter(settingValueEntity -> key.equals(settingValueEntity.getKey()))
				.map(MessagingSettingValueEmbeddable::getValue)
				.findAny()
				.orElse(null))
			.orElse(null);
	}
}
