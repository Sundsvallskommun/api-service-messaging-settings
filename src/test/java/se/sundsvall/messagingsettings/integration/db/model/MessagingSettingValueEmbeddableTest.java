package se.sundsvall.messagingsettings.integration.db.model;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import se.sundsvall.messagingsettings.integration.db.model.enums.ValueType;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

class MessagingSettingValueEmbeddableTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(MessagingSettingValueEmbeddable.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

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
