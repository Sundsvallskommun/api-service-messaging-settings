package se.sundsvall.messagingsettings.integration.employee.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.messagingsettings.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class EmployeePropertiesTest {

	@Autowired
	private EmployeeProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.connectTimeout()).isEqualTo(3);
		assertThat(properties.readTimeout()).isEqualTo(4);
	}
}
