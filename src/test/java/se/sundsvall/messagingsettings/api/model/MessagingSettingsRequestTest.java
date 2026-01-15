package se.sundsvall.messagingsettings.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class MessagingSettingsRequestTest {

	@Test
	void testBean() {
		assertThat(MessagingSettingsRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));

		assertThat(MessagingSettingsRequest.MessagingSettingValueRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderAndGetters() {
		// Arrange
		final var key = "department_name";
		final var value = "IT Department";
		final var type = "STRING";

		final var valueRequest = MessagingSettingsRequest.MessagingSettingValueRequest.builder()
			.withKey(key)
			.withValue(value)
			.withType(type)
			.build();

		final var request = MessagingSettingsRequest.builder()
			.withValues(List.of(valueRequest))
			.build();

		// Assert
		assertThat(request).isNotNull();
		assertThat(request.getValues()).hasSize(1);
		assertThat(request.getValues().getFirst().getKey()).isEqualTo(key);
		assertThat(request.getValues().getFirst().getValue()).isEqualTo(value);
		assertThat(request.getValues().getFirst().getType()).isEqualTo(type);
	}

	@Test
	void builderWithMultipleValues() {
		// Arrange
		final var value1 = MessagingSettingsRequest.MessagingSettingValueRequest.builder()
			.withKey("department_name")
			.withValue("IT Department")
			.withType("STRING")
			.build();

		final var value2 = MessagingSettingsRequest.MessagingSettingValueRequest.builder()
			.withKey("sms_enabled")
			.withValue("true")
			.withType("BOOLEAN")
			.build();

		final var value3 = MessagingSettingsRequest.MessagingSettingValueRequest.builder()
			.withKey("max_count")
			.withValue("100")
			.withType("NUMERIC")
			.build();

		final var request = MessagingSettingsRequest.builder()
			.withValues(List.of(value1, value2, value3))
			.build();

		// Assert
		assertThat(request.getValues()).hasSize(3);
		assertThat(request.getValues().get(0).getKey()).isEqualTo("department_name");
		assertThat(request.getValues().get(0).getType()).isEqualTo("STRING");
		assertThat(request.getValues().get(1).getKey()).isEqualTo("sms_enabled");
		assertThat(request.getValues().get(1).getType()).isEqualTo("BOOLEAN");
		assertThat(request.getValues().get(2).getKey()).isEqualTo("max_count");
		assertThat(request.getValues().get(2).getType()).isEqualTo("NUMERIC");
	}

	@Test
	void noArgsConstructor() {
		final var request = new MessagingSettingsRequest();
		assertThat(request).isNotNull();

		final var valueRequest = new MessagingSettingsRequest.MessagingSettingValueRequest();
		assertThat(valueRequest).isNotNull();
	}
}
