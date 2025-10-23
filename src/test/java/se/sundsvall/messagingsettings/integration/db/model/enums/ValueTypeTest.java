package se.sundsvall.messagingsettings.integration.db.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.messagingsettings.integration.db.model.enums.ValueType.BOOLEAN;
import static se.sundsvall.messagingsettings.integration.db.model.enums.ValueType.NUMERIC;
import static se.sundsvall.messagingsettings.integration.db.model.enums.ValueType.STRING;

import org.junit.jupiter.api.Test;

class ValueTypeTest {

	@Test
	void enums() {
		assertThat(ValueType.values()).containsExactlyInAnyOrder(BOOLEAN, NUMERIC, STRING);
	}

	@Test
	void enumValues() {
		assertThat(BOOLEAN).hasToString("BOOLEAN");
		assertThat(NUMERIC).hasToString("NUMERIC");
		assertThat(STRING).hasToString("STRING");
	}
}
