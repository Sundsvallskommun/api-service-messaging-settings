package se.sundsvall.messagingsettings.api.model.enums;

import org.junit.jupiter.api.Test;
import se.sundsvall.messagingsettings.enums.SnailMailMethod;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.messagingsettings.enums.SnailMailMethod.EMAIL;
import static se.sundsvall.messagingsettings.enums.SnailMailMethod.SC_ADMIN;

class SnailMailMethodTest {

	@Test
	void enums() {
		assertThat(SnailMailMethod.values()).containsExactlyInAnyOrder(EMAIL, SC_ADMIN);
	}

	@Test
	void enumValues() {
		assertThat(EMAIL).hasToString("EMAIL");
		assertThat(SC_ADMIN).hasToString("SC_ADMIN");
	}
}
