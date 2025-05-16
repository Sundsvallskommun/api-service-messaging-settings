package se.sundsvall.messagingsettings.integration.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.messagingsettings.test.annotation.UnitTest;

@UnitTest
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata.sql"
})
class MessagingSettingsRepositoryTest {

	@Autowired
	private MessagingSettingsRepository messagingSettingsRepository;

	@Test
	void findByMunicipalityIdAndDepartmentId() {
		final var id = 15L;
		final var municipalityId = "2281";
		final var departmentId = "SKM";

		final var result = messagingSettingsRepository.findByMunicipalityIdAndDepartmentId(municipalityId, departmentId);

		assertThat(result).isPresent().hasValueSatisfying(entity -> {
			assertThat(entity.getId()).isEqualTo(id);
			assertThat(entity.getMunicipalityId()).isEqualTo(municipalityId);
			assertThat(entity.getDepartmentId()).isEqualTo(departmentId);
		});

	}

	@Test
	void findByMunicipalityIdAndDepartmentId_notFound() {
		final var municipalityId = "NON_EXISTING";
		final var departmentId = "NON_EXISTING";

		final var result = messagingSettingsRepository.findByMunicipalityIdAndDepartmentId(municipalityId, departmentId);

		assertThat(result).isNotPresent();
	}
}
