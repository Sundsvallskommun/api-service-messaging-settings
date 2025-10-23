package se.sundsvall.messagingsettings.integration.db.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import se.sundsvall.messagingsettings.integration.db.model.enums.ValueType;

class MessagingSettingValueEmbeddableTest {

	@Test
	void builderAndGetters() {
		final var key = "key";
		final var type = ValueType.BOOLEAN;
		final var value = "value";

		final var entity = MessagingSettingValueEmbeddable.builder()
			.withKey(key)
			.withType(type)
			.withValue(value)
			.build();

		assertThat(entity).hasNoNullFieldsOrProperties();
		assertThat(entity.getKey()).isEqualTo(key);
		assertThat(entity.getType()).isEqualByComparingTo(type);
		assertThat(entity.getValue()).isEqualTo(value);
	}

	@Test
	void builderAndGetters_noValues() {
		assertThat(new MessagingSettingValueEmbeddable()).hasAllNullFieldsOrProperties();
		assertThat(MessagingSettingValueEmbeddable.builder().build()).hasAllNullFieldsOrProperties();
	}
}
