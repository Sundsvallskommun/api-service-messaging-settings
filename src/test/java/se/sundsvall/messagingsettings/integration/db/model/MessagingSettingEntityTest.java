package se.sundsvall.messagingsettings.integration.db.model;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class MessagingSettingEntityTest {

	@Test
	void builderAndGetters() {
		final var id = "475dcfd4-21d5-4f1d-9aac-fbf247f889b7";
		final var municipalityId = "2281";
		final var created = OffsetDateTime.now().minusWeeks(5);
		final var updated = OffsetDateTime.now();

		final var entity = MessagingSettingEntity.builder()
			.withId(id)
			.withMunicipalityId(municipalityId)
			.withCreated(created)
			.withUpdated(updated)
			.build();

		assertThat(entity).hasNoNullFieldsOrProperties();
		assertThat(entity.getId()).isEqualTo(id);
		assertThat(entity.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(entity.getCreated()).isEqualTo(created);
		assertThat(entity.getUpdated()).isEqualTo(updated);
	}

	@Test
	void builderAndGetters_noValues() {
		assertThat(new MessagingSettingEntity()).hasAllNullFieldsOrPropertiesExcept("values").extracting(MessagingSettingEntity::getValues).isEqualTo(emptyList());
		assertThat(MessagingSettingEntity.builder().build()).hasAllNullFieldsOrPropertiesExcept("values").extracting(MessagingSettingEntity::getValues).isEqualTo(emptyList());
	}
}
