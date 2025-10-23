package se.sundsvall.messagingsettings.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.messagingsettings.integration.db.model.enums.ValueType;

@Embeddable
@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class MessagingSettingValueEmbeddable {

	@Column(name = "`key`")
	private String key;

	@Column(name = "`value`", columnDefinition = "text")
	private String value;

	@Enumerated(EnumType.STRING)
	@Column(name = "`type`")
	private ValueType type;
}
