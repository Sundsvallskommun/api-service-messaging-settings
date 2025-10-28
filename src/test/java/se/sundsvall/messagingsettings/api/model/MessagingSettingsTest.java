package se.sundsvall.messagingsettings.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MessagingSettingsTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(MessagingSettings.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));

		assertThat(MessagingSettings.MessagingSettingValue.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderAndGetters() {
		final var created = OffsetDateTime.now().minusDays(7);
		final var id = "id";
		final var municipalityId = "municipalityId";
		final var updated = OffsetDateTime.now();
		final var key = "key";
		final var type = "type";
		final var value = "value";

		final var values = List.of(MessagingSettings.MessagingSettingValue.builder()
			.withKey(key)
			.withType(type)
			.withValue(value)
			.build());

		final var messagingSettings = MessagingSettings.builder()
			.withCreated(created)
			.withId(id)
			.withMunicipalityId(municipalityId)
			.withUpdated(updated)
			.withValues(values)
			.build();

		assertThat(messagingSettings).isInstanceOf(MessagingSettings.class).hasNoNullFieldsOrProperties();
		assertThat(messagingSettings.getCreated()).isEqualTo(created);
		assertThat(messagingSettings.getId()).isEqualTo(id);
		assertThat(messagingSettings.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(messagingSettings.getUpdated()).isEqualTo(updated);
		assertThat(messagingSettings.getValues()).usingRecursiveComparison().isEqualTo(values);
	}

	@Test
	void builderAndGetters_noValues() {
		assertThat(MessagingSettings.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new MessagingSettings()).hasAllNullFieldsOrProperties();

		assertThat(MessagingSettings.MessagingSettingValue.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new MessagingSettings.MessagingSettingValue()).hasAllNullFieldsOrProperties();
	}
}
