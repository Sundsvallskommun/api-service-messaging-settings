package se.sundsvall.messagingsettings.integration.db.entity;

import static org.hibernate.Length.LONG;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;
import org.hibernate.annotations.UpdateTimestamp;
import se.sundsvall.messagingsettings.enums.SnailMailMethod;

@Entity
@Table(name = "messaging_settings", indexes = {
	@Index(name = "idx_messaging_settings_municipality_id_department_id", columnList = "municipality_id, department_id"),
	@Index(name = "idx_messaging_settings_municipality_id_namespace", columnList = "municipality_id, namespace"),
	@Index(name = "idx_messaging_settings_municipality_id_namespace_department_id", columnList = "municipality_id, namespace, department_id"),
	@Index(name = "idx_messaging_settings_municipality_id_namespace_department_name", columnList = "municipality_id, namespace, department_name")
})
@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class MessagingSettingsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", length = 36)
	private String id;

	@Column(name = "municipality_id")
	private String municipalityId;

	@Column(name = "namespace")
	private String namespace;

	@Column(name = "organization_number", length = 12)
	private String organizationNumber;

	@Column(name = "department_id")
	private String departmentId;

	@Column(name = "department_name")
	private String departmentName;

	@Enumerated(EnumType.STRING)
	@Column(name = "snail_mail_method")
	private SnailMailMethod snailMailMethod;

	@Column(name = "callback_email")
	private String callbackEmail;

	@Column(name = "support_text", length = LONG)
	private String supportText;

	@Column(name = "contact_information_url")
	private String contactInformationUrl;

	@Column(name = "contact_information_phone_number")
	private String contactInformationPhoneNumber;

	@Column(name = "contact_information_email")
	private String contactInformationEmail;

	@Column(name = "contact_information_email_name")
	private String contactInformationEmailName;

	@Column(name = "sms_sender")
	private String smsSender;

	@Column(name = "folder_name")
	private String folderName;

	@CreationTimestamp
	@Column(name = "created")
	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
	private OffsetDateTime created;

	@UpdateTimestamp
	@Column(name = "updated")
	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
	private OffsetDateTime updated;

	@Column(name = "sms_enabled")
	private boolean smsEnabled;

	@Column(name = "rek_enabled")
	private boolean rekEnabled;
}
