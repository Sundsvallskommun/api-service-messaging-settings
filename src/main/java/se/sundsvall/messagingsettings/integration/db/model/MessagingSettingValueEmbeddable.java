package se.sundsvall.messagingsettings.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.sundsvall.messagingsettings.integration.db.model.enums.ValueType;

@Embeddable
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MessagingSettingValueEmbeddable {

	@Column(name = "`key`", nullable = false)
	private String key;

	@Column(name = "`value`", columnDefinition = "text", nullable = false)
	private String value;

	@Enumerated(EnumType.STRING)
	@Column(name = "`type`", nullable = false)
	private ValueType type;

	@Override
	public int hashCode() {
		return Objects.hash(key, type, value);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final MessagingSettingValueEmbeddable other)) {
			return false;
		}
		return Objects.equals(key, other.key) && type == other.type && Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return "MessagingSettingValueEmbeddable [key=" + key + ", value=" + value + ", type=" + type + "]";
	}
}
