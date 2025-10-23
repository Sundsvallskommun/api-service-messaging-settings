package se.sundsvall.messagingsettings.integration.db.model;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.UUID;
import static org.hibernate.annotations.TimeZoneStorageType.NORMALIZE_UTC;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "messaging_setting", indexes = {
	@Index(name = "idx_messaging_setting_municipality_id", columnList = "municipality_id")
})
@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class MessagingSettingEntity {

	@Id
	@GeneratedValue(strategy = UUID)
	@Column(name = "id", length = 36)
	private String id;

	@Column(name = "municipality_id")
	private String municipalityId;

	@CreationTimestamp
	@Column(name = "created")
	@TimeZoneStorage(NORMALIZE_UTC)
	private OffsetDateTime created;

	@UpdateTimestamp
	@Column(name = "updated")
	@TimeZoneStorage(NORMALIZE_UTC)
	private OffsetDateTime updated;

	@Builder.Default
	@ElementCollection(fetch = EAGER)
	@CollectionTable(name = "messaging_setting_value", indexes = {
		@Index(name = "idx_messaging_setting_value_messaging_setting_id_key", columnList = "messaging_setting_id, `key`")
	}, uniqueConstraints = {
		@UniqueConstraint(name = "uk_messaging_setting_id_key_value", columnNames = {
			"messaging_setting_id", "`key`", "`value`"
		})
	}, joinColumns = @JoinColumn(name = "messaging_setting_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_messaging_setting_value_messaging_setting")))
	private List<MessagingSettingValueEmbeddable> values = new ArrayList<>();
}
