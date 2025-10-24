package se.sundsvall.messagingsettings.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import java.time.OffsetDateTime;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MessagingSettingEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(MessagingSettingEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

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
