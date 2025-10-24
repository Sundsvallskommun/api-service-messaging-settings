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
	private static final String KEY_CALLBACK_EMAIL = "callback_email";
	private static final String KEY_CONTACT_INFORMATION_URL = "contact_information_url";
	private static final String KEY_CONTACT_INFORMATION_PHONE_NUMBER = "contact_information_phone_number";
	private static final String KEY_CONTACT_INFORMATION_EMAIL = "contact_information_email";
	private static final String KEY_CONTACT_INFORMATION_EMAIL_NAME = "contact_information_email_name";
	private static final String KEY_DEPARTMENT_NAME = "department_name";
	private static final String KEY_ORGANIZATION_NUMBER = "organization_number";
	private static final String KEY_SMS_SENDER = "sms_sender";
	private static final String KEY_SUPPORT_TEXT = "support_text";
	private static final String KEY_FOLDER_NAME = "folder_name";

	private static final String KEY_SNAIL_MAIL_METHOD = "snail_mail_method";
	private static final String KEY_REK_ENABLED = "rek_enabled";
	private static final String KEY_SMS_ENABLED = "sms_enabled";

	private EntityMapper() {}

	public static SenderInfoResponse toSenderInfo(final MessagingSettingEntity nullableSettingEntity) {
		return ofNullable(nullableSettingEntity)
			.map(settingEntity -> SenderInfoResponse.builder()
				.withContactInformationUrl(fetchSettingValue(settingEntity.getValues(), KEY_CONTACT_INFORMATION_URL))
				.withContactInformationPhoneNumber(fetchSettingValue(settingEntity.getValues(), KEY_CONTACT_INFORMATION_PHONE_NUMBER))
				.withContactInformationEmail(fetchSettingValue(settingEntity.getValues(), KEY_CONTACT_INFORMATION_EMAIL))
				.withContactInformationEmailName(fetchSettingValue(settingEntity.getValues(), KEY_CONTACT_INFORMATION_EMAIL_NAME))
				.withOrganizationNumber(fetchSettingValue(settingEntity.getValues(), KEY_ORGANIZATION_NUMBER))
				.withSmsSender(fetchSettingValue(settingEntity.getValues(), KEY_SMS_SENDER))
				.withSupportText(fetchSettingValue(settingEntity.getValues(), KEY_SUPPORT_TEXT))
				.withFolderName(fetchSettingValue(settingEntity.getValues(), KEY_FOLDER_NAME))
				.build())
			.orElse(null);
	}

	public static CallbackEmailResponse toCallbackEmail(final MessagingSettingEntity nullableSettingEntity) {
		return ofNullable(nullableSettingEntity)
			.map(settingEntity -> CallbackEmailResponse.builder()
				.withCallbackEmail(fetchSettingValue(settingEntity.getValues(), KEY_CALLBACK_EMAIL))
				.withOrganizationNumber(fetchSettingValue(settingEntity.getValues(), KEY_ORGANIZATION_NUMBER))
				.build())
			.orElse(null);
	}

	public static PortalSettingsResponse toPortalSettings(final MessagingSettingEntity nullableSettingEntity) {
		return ofNullable(nullableSettingEntity)
			.map(settingEntity -> PortalSettingsResponse.builder()
				.withDepartmentName(fetchSettingValue(settingEntity.getValues(), KEY_DEPARTMENT_NAME))
				.withMunicipalityId(settingEntity.getMunicipalityId())
				.withOrganizationNumber(fetchSettingValue(settingEntity.getValues(), KEY_ORGANIZATION_NUMBER))
				.withSnailMailMethod(ofNullable(fetchSettingValue(settingEntity.getValues(), KEY_SNAIL_MAIL_METHOD)).map(SnailMailMethod::valueOf).orElse(null))
				.withRekEnabled(ofNullable(fetchSettingValue(settingEntity.getValues(), KEY_REK_ENABLED)).map(Boolean::valueOf).orElse(null))
				.withSmsEnabled(ofNullable(fetchSettingValue(settingEntity.getValues(), KEY_SMS_ENABLED)).map(Boolean::valueOf).orElse(null))
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
